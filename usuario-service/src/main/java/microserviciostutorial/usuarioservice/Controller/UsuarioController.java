package microserviciostutorial.usuarioservice.Controller;


import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import microserviciostutorial.usuarioservice.Entidades.Usuario;
import microserviciostutorial.usuarioservice.Modelos.Carro;
import microserviciostutorial.usuarioservice.Modelos.Moto;
import microserviciostutorial.usuarioservice.Service.UsuarioService;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuario(){
        List<Usuario> usuarios = usuarioService.getAll();
        if(usuarios.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usuarios);
    }


    @GetMapping("/{id}")
    public Optional<ResponseEntity<Usuario>> obtenerUsuario(@PathVariable("id") int id){
        
        return usuarioService.getUsuarioById(id).map(usuarioVerdadero->ResponseEntity.ok().body(usuarioVerdadero));

    }

    /*Manejamos circuitbreaker para que cuando un microservicio falle, se llame a otro microservicio, en este caso a un metodo llamado getCarros */
    @CircuitBreaker(name = "carrosCB", fallbackMethod = "fallBackGetCarros")
    @GetMapping("/carros/{usuarioId}")
    public ResponseEntity<List<Carro>> listarCarros(@PathVariable("usuarioId") int id){
        Optional<Usuario> usuario = usuarioService.getUsuarioById(id);
        if(usuario == null){
            return ResponseEntity.notFound().build();
        }
        List<Carro> carros = usuarioService.getCarros(id);
        return ResponseEntity.ok().body(carros);
    }



    @CircuitBreaker(name = "motosCB", fallbackMethod = "fallBackGetMotos")
    @GetMapping("/motos/{usuarioId}")
    public ResponseEntity<List<Moto>> listarMotos(@PathVariable("usuarioId") int id){
        Optional<Usuario> usuario = usuarioService.getUsuarioById(id);
        if(usuario == null){
            return ResponseEntity.notFound().build();
        }
        List<Moto> motos = usuarioService.getMotos(id);
        return ResponseEntity.ok().body(motos);
    }



    @PostMapping
    public ResponseEntity<Usuario> saveUuser(@RequestBody Usuario usuario){
        Usuario nuevoUsuario = usuarioService.save(usuario);
        return ResponseEntity.ok(nuevoUsuario);
    }

    @CircuitBreaker(name = "carrosCB", fallbackMethod = "fallBackSaveCarros")
    @PostMapping("/carro/{usuarioId}")
    public ResponseEntity<Carro> guardarCarro(@PathVariable("usuarioId") int usuarioId, @RequestBody Carro carro){
        Carro nuevoCarro = usuarioService.saveCarro(usuarioId, carro);
        return ResponseEntity.ok(nuevoCarro);
    }


    @CircuitBreaker(name = "motosCB", fallbackMethod = "fallBackSaveMotos")
    @PostMapping("/moto/{usuarioId}")
    public ResponseEntity<Moto> guardarMoto(@PathVariable("usuarioId") int usuarioId, @RequestBody Moto moto){
        Moto nuevaMoto = usuarioService.saveMoto(usuarioId, moto);
        return ResponseEntity.ok(nuevaMoto);
    }


    /*Con este metodo se uso feigncliente */
    @CircuitBreaker(name = "todosCB", fallbackMethod = "fallBackGetTodos")
    @GetMapping("/todos/{usuarioId}")
    public ResponseEntity<Map<String,Object>> listarTodosLosVehiculos(@PathVariable("usuarioId") int usuarioId){
        Map<String,Object> resultado = usuarioService.getUsuarioAndVehiculos(usuarioId);
        return ResponseEntity.ok().body(resultado);
    }




    /*Es como si no pudieras llamar al micro porque los autos estan en el "taller" */
    private ResponseEntity<List<Carro>> fallBackGetCarros(@PathVariable("usuarioId") int id, RuntimeException excepcion){
        return new ResponseEntity("El usuario : "+ id+ " tiene los carros en el taller ", HttpStatus.OK);
    }

    private ResponseEntity<Carro> fallBackSaveCarro(@PathVariable("usuarioId") int id,@RequestBody Carro carro, RuntimeException excepcion){
        return new ResponseEntity("El usuario con id:"+id+ "no tiene dinero para los carros", HttpStatus.OK);
    }



    private ResponseEntity<Carro> fallBackGetMotos(@PathVariable("usuarioId") int id, RuntimeException excepcion){
        return new ResponseEntity("El usuario con id:"+id+ "tiene las motos en el taller", HttpStatus.OK);
    }

    private ResponseEntity<Carro> fallBackSaveMoto(@PathVariable("usuarioId") int id,@RequestBody Moto moto, RuntimeException excepcion){
        return new ResponseEntity("El usuario con id:"+id+ "no tiene dinero para las motos", HttpStatus.OK);
    }


    private ResponseEntity<Map<String, Object>> fallBackGetTodos(@PathVariable("usuarioId") int id,RuntimeException excepcion){
        return new ResponseEntity("El usuario con id:"+id+ "tiene los vehiculos en el taller", HttpStatus.OK);
    }


}