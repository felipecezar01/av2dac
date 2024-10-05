package com.av2dac;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    // Injeção de dependência do repositório
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Endpoint de teste
    @GetMapping("/test")
    public String testEndpoint() {
        return "API is working!";
    }

    // Listar todos os usuários
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        return ResponseEntity.ok(users); // Retorna a lista de usuários
    }

    // Criar um novo usuário
    @PostMapping("/add")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(savedUser); // Retorna o usuário salvo
    }

    // Atualizar um usuário existente
    @PutMapping("/update/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User userToUpdate = user.get();
            userToUpdate.setName(userDetails.getName());
            userToUpdate.setEmail(userDetails.getEmail());
            userToUpdate.setPassword(userDetails.getPassword());
            User updatedUser = userRepository.save(userToUpdate);
            return ResponseEntity.ok(updatedUser); // Retorna o usuário atualizado
        } else {
            return ResponseEntity.notFound().build(); // Retorna 404 se o usuário não for encontrado
        }
    }

    // Deletar um usuário
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.delete(user.get());
            return ResponseEntity.noContent().build(); // Retorna 204 se o usuário for deletado
        } else {
            return ResponseEntity.notFound().build(); // Retorna 404 se o usuário não for encontrado
        }
    }
}
