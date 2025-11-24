Projeto Caminhão de Lixo Inteligente

Sistema desenvolvido como projeto de final de semestre, que simula o trajeto de um caminhão de lixo
e notifica os moradores quando o caminhão estiver se aproximando do endereço cadastrado.



1) Funcionalidades

- Cadastro e login de usuários
- Registro de endereço do usuário
- Simulação do trajeto do caminhão de lixo
- Verificação de proximidade do caminhão em relação ao endereço
- Notificação ao usuário quando o caminhão estiver chegando
- (Opcional) Atualização em tempo real via WebSocket



2) Fluxo básico do sistema

Cadastro do usuário → endereço salvo no banco.

Cadastro da rota do caminhão → pontos de GPS simulados ou importados.

Simulador de movimento → Spring Boot atualiza a posição do caminhão a cada X segundos.

Verificação de proximidade → quando a posição do caminhão está próxima do endereço de um usuário, o sistema dispara uma notificação.

Usuário recebe alerta → vai colocar o lixo para fora.



3) Tecnologias Utilizadas

- Java 21
- Spring Boot 3.5.5
- Maven
- Spring Web
- Spring Data JPA
- H2 Database (desenvolvimento)
- PostgreSQL (produção)
- Lombok
- Spring WebSocket (opcional, tempo real)



4) Como Rodar o Projeto

- Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/caminhao-lixo.git
   ```

- Entre na pasta do projeto:
   ```bash
   cd caminhao-lixo
   ```

- Compile e rode:
   ```bash
   mvn spring-boot:run
   ```

- Acesse no navegador:
   ```
   http://localhost:8080
   ```

- Acesse o console do banco H2:
   ```
   http://localhost:8080/h2-console
   (usuário: sa / senha: )
   ```



5) Endpoints da API

### Cadastro de usuário
`POST /usuarios/cadastro`
```json
{
  "nome": "João da Silva",
  "email": "joao@email.com",
  "senha": "123456",
  "endereco": "Rua das Flores, 123"
}



6) Login
`POST /usuarios/login?email=joao@email.com&senha=123456`
- Resposta 200: "Login realizado com sucesso"
- Resposta 401: "Credenciais inválidas"



7) Autores
- Davi Patricio – [Github: Daviss0](https://github.com/Daviss0)
- Elivelton Sampaio - [Github: sampadev](https://github.com/sampadev)
- Luanderson Amparo - [Github: LuandersonAmparo](https://github.com/LuandersonAmparo)
- Luiz Felipe - [Github: LzcardosoZ](https://github.com/LzcardosoZ)
- Lucas Santos - [Github: lucassgg1](https://github.com/lucassgg1)