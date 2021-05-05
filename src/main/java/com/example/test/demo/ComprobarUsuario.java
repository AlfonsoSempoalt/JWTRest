package com.example.test.demo;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.impl.JWTParser;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.test.dao.UsuarioDAO;
import com.example.test.entidades.Usuario;
import com.google.gson.JsonObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/usuario/")
public class ComprobarUsuario {
    private String tkn = null;

    @GetMapping("/validar")
    public ResponseEntity<?> helloWorld2() {
        return validar("admin", "admin");
    }

    @GetMapping("/getUsers")
    public ResponseEntity<?> getUsers() {
        if (this.tkn != null) {
            List<Usuario> clientes = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                clientes.add(new Usuario(i, "user" + i, "123123123" + i));
            }
            return new ResponseEntity<>(clientes, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }


    @PostMapping("/validar")
    public ResponseEntity<?> iniciarSesion(@RequestBody Usuario usuario) {
        return validar(usuario.getUsername(), usuario.getPassword());
    }


    @GetMapping("/cerrarSesion")
    public String cerrarSesion() {
        this.tkn = null;
        return ("<h1> sesión cerrada <h1>");
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
            this.tkn = token;
            verificarToken(token);
        } catch (JWTCreationException e) {
            System.err.println(e.getMessage());
        }
        return token;
    }

    private void verificarToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256("secret");
            JWTVerifier verification = JWT.require(algorithm)
                    .withIssuer("auth0").build();
            DecodedJWT jwt = verification.verify(token);
            System.out.println(getUsername(token));
        } catch (JWTVerificationException e) {
            System.err.println(e.getMessage());
        }

    }

    public static String getUsername(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("usuario").asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }


}
