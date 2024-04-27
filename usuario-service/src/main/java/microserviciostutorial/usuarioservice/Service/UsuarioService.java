package microserviciostutorial.usuarioservice.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import microserviciostutorial.usuarioservice.Entidades.Usuario;
import microserviciostutorial.usuarioservice.FeignClients.CarroFeignClient;
import microserviciostutorial.usuarioservice.FeignClients.MotoFeignClient;
import microserviciostutorial.usuarioservice.Modelos.Carro;
import microserviciostutorial.usuarioservice.Modelos.Moto;
import microserviciostutorial.usuarioservice.Repositories.UsuarioRepository;

@Service
public class UsuarioService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MotoFeignClient motoFeign;

    @Autowired
    private CarroFeignClient carroFeign;

    @Autowired
    private UsuarioRepository usuarioRepo;
 
    public List<Carro> getCarros(int usuarioId){
        List<Carro> carros = restTemplate.getForObject("http://carro-service/carro/usuario/"+usuarioId, List.class);
        return carros;
    }

    public List<Moto> getMotos(int usuarioId){
        List<Moto> motos = restTemplate.getForObject("http://moto-service/moto/usuario/"+usuarioId, List.class);
        return motos;
    }

    public List<Usuario> getAll(){
        return usuarioRepo.findAll();
    }

    public Optional<Usuario> getUsuarioById(int id){
        return usuarioRepo.findById(id);
    }

    public Usuario save(Usuario user){
        Usuario nuevoUsuario = usuarioRepo.save(user);
        return nuevoUsuario;
    }

    public Carro saveCarro(int usuarioId, Carro carro){
        carro.setUsuarioId(usuarioId);
        Carro nuevCarro = carroFeign.save(carro);
        return nuevCarro;
    }

    public Moto saveMoto(int usuarioId, Moto moto){
        moto.setUsuarioId(usuarioId);
        Moto nuevaMoto = motoFeign.save(moto);
        return nuevaMoto;
    }

    /* Con este metodo obtenemos las motos y carros que pertenezcan a un usuario */
    public Map<String, Object> getUsuarioAndVehiculos(int usuarioId){
        Map<String, Object> resultado = new HashMap<>();
        Usuario usuario = usuarioRepo.getById(usuarioId);
        if(usuario == null){
            resultado.put("Mensaje", "El usuario no existe");
            return resultado;
        }
        resultado.put("Usuario", usuario);
        List<Carro> carros = carroFeign.getCarros(usuarioId);
        if(carros.isEmpty()){
            resultado.put("Carros", "El usuario no tiene carros");
        }else{
            resultado.put("Carros", carros);
        }

        List<Moto> motos = motoFeign.getmotos(usuarioId);
        if(motos.isEmpty()){
            resultado.put("Motos", "El usuario no tiene motos");
        }else{
            resultado.put("Motos", motos);
        }
        return resultado;
    }
}
