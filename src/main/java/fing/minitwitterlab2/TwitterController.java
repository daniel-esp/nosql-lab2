package fing.minitwitterlab2;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;

import fing.minitwitterlab2.datatypes.DataComentario;
import fing.minitwitterlab2.datatypes.DataUsuario;
import fing.minitwitterlab2.entidades.Comentario;
import fing.minitwitterlab2.entidades.Usuario;
import redis.clients.jedis.Jedis;

@RestController
public class TwitterController {
	
	MongoClientURI connectionString = new MongoClientURI("mongodb://localhost:27017");
    MongoClient mongoClient = new MongoClient(connectionString);
    CodecRegistry pojoCodecRegistry = org.bson.codecs.configuration.CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
    									org.bson.codecs.configuration.CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()));
    MongoDatabase database = mongoClient.getDatabase("MiniTwitter").withCodecRegistry(pojoCodecRegistry); 
	MongoCollection<Usuario> usuarios = database.getCollection("Usuarios", Usuario.class);
	MongoCollection<Comentario> comentarios = database.getCollection("Comentarios", Comentario.class);
	
	Jedis jedis = new Jedis();
	
	
	@RequestMapping(method = RequestMethod.POST, consumes = { "application/JSON" }, 
					produces = { "application/JSON" }, value = "/usuarios")
    public ResponseEntity<?> altaUsuario(@RequestBody DataUsuario user)
	{
		Gson gson = new Gson();
		String json = jedis.get(user.getEmail()); // Obtenemos el usuario desde redis
		Usuario usuario = gson.fromJson(json, Usuario.class);
		if (usuario == null) {
			ObjectId id = new ObjectId();
			Usuario nuevo = new Usuario(id, user.getEmail());
			usuarios.insertOne(nuevo);
			
			String nuevo_json = gson.toJson(nuevo);
			jedis.set(nuevo.getEmail(), nuevo_json);  //En redis, la clave para los usuarios es su email.
			
			return new ResponseEntity<>(HttpStatus.CREATED);
		} else
			return new ResponseEntity<>("Ya existe un usuario con email "+user.getEmail(),
												HttpStatus.INTERNAL_SERVER_ERROR);
    }
	
	@RequestMapping(method = RequestMethod.POST, consumes = { "application/JSON" }, 
					produces = { "application/JSON" }, value = "/comentarios")
    public ResponseEntity<?> altaComentario(@RequestBody DataComentario comment)
	{
		Gson gson = new Gson();
		String json = jedis.get(comment.getEmailUsuario()); // Obtenemos el usuario desde redis
		Usuario usuario = gson.fromJson(json, Usuario.class);
		if (usuario != null) {
			if (comment.getTexto().length() < 256) {
				ObjectId id = new ObjectId();
				Comentario nuevo = new Comentario(id, comment.getTexto(), comment.getEmailUsuario(), null);
				comentarios.insertOne(nuevo);
				
				String nuevo_json = gson.toJson(nuevo);
				jedis.set(nuevo.getId().toString(), nuevo_json);
				
				return new ResponseEntity<>(HttpStatus.CREATED);
			} else
				return new ResponseEntity<>("El texto debe ser menor a 256 caracteres.",
						HttpStatus.INTERNAL_SERVER_ERROR);
		} else
			return new ResponseEntity<>("No existe un usuario con email "+comment.getEmailUsuario(),
					HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@RequestMapping(method = RequestMethod.POST, consumes = { "application/JSON" }, 
			produces = { "application/JSON" }, value = "/comentarios/{id}/comentarios")
	public ResponseEntity<?> comentarComentario(@PathVariable("id") String id, @RequestBody DataComentario comment)
	{
		Gson gson = new Gson();
		String json = jedis.get(id); // Obtenemos el comentario desde redis
		Comentario comentario = gson.fromJson(json, Comentario.class);
		if(comentario != null) {
			json = jedis.get(comment.getEmailUsuario());
			Usuario usuario = gson.fromJson(json, Usuario.class);
			if (usuario != null) {
				if (comment.getTexto().length() < 256) {
					ObjectId id_nuevo = new ObjectId();
					ObjectId padre = new ObjectId(id);
					Comentario nuevo = new Comentario(id_nuevo, comment.getTexto(), comment.getEmailUsuario(), padre);
					comentarios.insertOne(nuevo);
					
					String nuevo_json = gson.toJson(nuevo);
					jedis.set(nuevo.getId().toString(), nuevo_json);
					
					return new ResponseEntity<>(HttpStatus.CREATED);
				} else
					return new ResponseEntity<>("El texto debe ser menor a 256 caracteres.",
							HttpStatus.INTERNAL_SERVER_ERROR);
			} else
				return new ResponseEntity<>("No existe un usuario con email "+comment.getEmailUsuario(),
						HttpStatus.INTERNAL_SERVER_ERROR);
		} else
			return new ResponseEntity<>("No existe el comentario con id "+id,
					HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/comentarios", produces = { "application/JSON" })
    public ResponseEntity<?> listarComentarios(@RequestParam("email") String email)
	{
		Gson gson = new Gson();
		String json = jedis.get(email); // Obtenemos el usuario desde redis
		Usuario usuario = gson.fromJson(json, Usuario.class);
		List<DataComentario> res = new ArrayList<>();
		if(usuario != null) {
			MongoCursor<Comentario> cursor = comentarios.find(eq("emailUsuario",email)).iterator();
			while(cursor.hasNext()) {
				Comentario com = (Comentario)cursor.next();
				res.add(com.getDatatype());
			}
			return new ResponseEntity<>(res, HttpStatus.OK);
		} else
			return new ResponseEntity<>("No existe un usuario con email "+email,
					HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/comentarios/{id}", produces = { "application/JSON" })
    public ResponseEntity<?> getComentario(@PathVariable("id") String id)
	{
		List<DataComentario> lista = new ArrayList<>();
		Gson gson = new Gson();
		String json = jedis.get(id); // Obtenemos el comentario desde redis
		Comentario comentario = gson.fromJson(json, Comentario.class);
		if(comentario != null) {
			MongoCursor<Comentario> cursor = comentarios.find(eq("comentarioPadre", new ObjectId(id))).iterator();
			while(cursor.hasNext()) {
				Comentario com = (Comentario)cursor.next();
				lista.add(com.getDatatype());
			}
			DataComentario res = comentario.getDatatype();
			res.setComentarios(lista);
			return new ResponseEntity<>(res, HttpStatus.OK);
		} else
			return new ResponseEntity<>("No existe el comentario con id "+id,
					HttpStatus.INTERNAL_SERVER_ERROR);
	}
    
}
