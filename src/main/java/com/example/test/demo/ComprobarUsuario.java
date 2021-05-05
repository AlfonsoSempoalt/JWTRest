package com.example.test.demo;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.test.dao.UsuarioDAO;
import com.example.test.entidades.Usuario;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/usuario/")
public class ComprobarUsuario {
    private final Gson gson = new Gson();

    @GetMapping("/validar")
    public ResponseEntity<?> helloWorld2() {

        return validar("adminasd", "admin");
    }

    @PostMapping("/validar")
    public ResponseEntity<?> comprobarUsuario(@RequestBody Usuario usuario) {
        return validar(usuario.getUsername(),usuario.getPassword());
    }


    private ResponseEntity<?> validar(String usuario, String password) {

        boolean status = UsuarioDAO.
                validar(usuario, password);
        if (status) {
            String token = this.crearToken(usuario, password);
            JsonObject json = new JsonObject();
            json.addProperty("token", token);
            return new ResponseEntity<>(json.toString(), HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
    }

    private String crearToken(String user, String password) {
        String token = null;
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            token = JWT.create().withIssuer("auth0").
                    withClaim("usuario", user).sign(algorithm);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return token;
    }

    private JsonObject stringToJson(String token) {
        return new JsonParser().parse(token).getAsJsonObject();
    }

}
