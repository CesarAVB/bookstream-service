## [1.10.0](https://github.com/CesarAVB/bookstream-service/compare/v1.9.0...v1.10.0) (2026-02-24)

### Features

* Renomeia tabela de usuários para cliente_auth ([e30d47d](https://github.com/CesarAVB/bookstream-service/commit/e30d47d967c11697f85fb97701720c2f8391d9df))

## [1.9.0](https://github.com/CesarAVB/bookstream-service/compare/v1.8.0...v1.9.0) (2026-02-24)

### Features

* Adiciona AuthController com endpoints de login e registro de ([f928d82](https://github.com/CesarAVB/bookstream-service/commit/f928d82d25e9fd6836905d822d3ff4a59ea740fe))
* Adiciona AuthService com métodos de login e registro de usuários ([4d9f03b](https://github.com/CesarAVB/bookstream-service/commit/4d9f03bd5fc36cdf54874f4110865a849786cc50))
* Adiciona configuração de JWT no application-prod.properties ([8ba6896](https://github.com/CesarAVB/bookstream-service/commit/8ba6896708cc7295782acce1d0913939f856979b))
* Adiciona dependências do JJWT para criação e validação de tokens ([9ab8906](https://github.com/CesarAVB/bookstream-service/commit/9ab8906c0343326595426bc8f15c1b4b15f5ed88))
* Adiciona DTOs para requisição e resposta de login ([84943e3](https://github.com/CesarAVB/bookstream-service/commit/84943e328fa09d356a4cd5c09782e0c0c62457c8))
* Adiciona filtro de autenticação JWT para validação de tokens ([22122a1](https://github.com/CesarAVB/bookstream-service/commit/22122a11cf4b093e4783a9b76ed7cbe1bd08e16b))
* Adiciona JwtService para geração e validação de tokens JWT ([28cac12](https://github.com/CesarAVB/bookstream-service/commit/28cac1205f3d23c54f287d23aa559a81554f4745))
* Remove UsuarioController and its associated endpoints ([154e7ad](https://github.com/CesarAVB/bookstream-service/commit/154e7adb99d86d105bf7ec2e2800e5e6a5f409e4))

## [1.8.0](https://github.com/CesarAVB/bookstream-service/compare/v1.7.0...v1.8.0) (2026-02-24)

### Features

* Atualiza CHANGELOG.md para versão 1.7.0 com novas funcionalidades ([5dccb58](https://github.com/CesarAVB/bookstream-service/commit/5dccb58157994d9f580a0006976172e183584503))
* Atualiza configuração do banco de dados no ([968e2e4](https://github.com/CesarAVB/bookstream-service/commit/968e2e4168362e66acc61c215f1ce8891ec10a94))
* Corrige permissão do Maven Wrapper e melhora cache de build ([6a24b48](https://github.com/CesarAVB/bookstream-service/commit/6a24b481efec6c318391b3984a522740a9dac51f))

## [1.7.0](https://github.com/CesarAVB/bookstream-service/compare/v1.6.0...v1.7.0) (2026-02-24)

### Features

* Atualiza CHANGELOG.md para versão 1.6.0 com novas funcionalidades ([ed62b22](https://github.com/CesarAVB/bookstream-service/commit/ed62b22a358af12caa2dbf2c92a20312ae7082bb))
* Substitui docker-compose.yml por docker-compose.yaml ([5412735](https://github.com/CesarAVB/bookstream-service/commit/5412735d0986ffcae9fc5c158a2df14499098bbe))

## [1.6.0](https://github.com/CesarAVB/bookstream-service/compare/v1.5.1...v1.6.0) (2026-02-24)

### Features

* Adiciona classes de requisição e resposta para o cadastro de ([6778f32](https://github.com/CesarAVB/bookstream-service/commit/6778f3265895363bc80af32f982c168d9141161f))
* Adiciona configuração de segurança com JWT e BCryptPasswordEncoder ([0aab390](https://github.com/CesarAVB/bookstream-service/commit/0aab39075a9ae83a67d7cb88c6a88c2c6d6d388e))
* Adiciona dependência do Spring Boot Starter Security para ([79bc5a2](https://github.com/CesarAVB/bookstream-service/commit/79bc5a27f725e6b7632393075d067c6edfcce8a4))
* Adiciona o controlador UsuarioController para gerenciamento de ([3c580bb](https://github.com/CesarAVB/bookstream-service/commit/3c580bbbabaafcd82bc8ef51ee289f780ca1a304))
* Cria a classe Usuario para representar usuários no sistema ([0467775](https://github.com/CesarAVB/bookstream-service/commit/0467775a90fd9a996429d96410e4dc504affe528))
* Cria a classe UsuarioService para gerenciamento de usuários ([73b4c89](https://github.com/CesarAVB/bookstream-service/commit/73b4c89167999b0428e5a26c70625e5a512b48f5))
* Cria a entidade Usuario com atributos e anotações JPA ([92022e0](https://github.com/CesarAVB/bookstream-service/commit/92022e0aae439d3decf45f739d61af0b1f90685b))
* Cria o repositório UsuarioRepository para gerenciamento de ([baa7caa](https://github.com/CesarAVB/bookstream-service/commit/baa7caa03380474d840abe3f0879fe9715b01bd5))

## [1.5.1](https://github.com/CesarAVB/bookstream-service/compare/v1.5.0...v1.5.1) (2026-02-23)

### Bug Fixes

* Ativa a chave de configuração do bucket do Minio para ([cfba2e1](https://github.com/CesarAVB/bookstream-service/commit/cfba2e161fbe6198b0e76edc4ffc76200b7f59af))
* Corrige a chave de configuração do bucket do Minio para ([2b3764c](https://github.com/CesarAVB/bookstream-service/commit/2b3764c9677b35ad7d855eee98ea6046e6ea9eff))

## [1.5.0](https://github.com/CesarAVB/bookstream-service/compare/v1.4.0...v1.5.0) (2026-02-23)

### Features

* Adiciona arquivo docker-compose.yml para configuração do serviço ([4be1e29](https://github.com/CesarAVB/bookstream-service/commit/4be1e292653be03fe77cf8d3dbdff7a155658383))

## [1.4.0](https://github.com/CesarAVB/bookstream-service/compare/v1.3.0...v1.4.0) (2026-02-23)

### Features

* Adiciona permissão de execução para o script mvnw no workflow de ([800fc8f](https://github.com/CesarAVB/bookstream-service/commit/800fc8fac62421c406652b7e1f208127972d67e9))

## [1.3.0](https://github.com/CesarAVB/bookstream-service/compare/v1.2.0...v1.3.0) (2026-02-23)

### Features

* Atualiza workflow de publicação Docker para acionar em lançamentos ([859e82e](https://github.com/CesarAVB/bookstream-service/commit/859e82ebdadff064091ab434ea29156145244118))

## [1.2.0](https://github.com/CesarAVB/bookstream-service/compare/v1.1.0...v1.2.0) (2026-02-23)

### Features

* Adiciona classe ErrorResponse para padronização de respostas de ([b7c7bda](https://github.com/CesarAVB/bookstream-service/commit/b7c7bdada975d8b7b38871aab100545e305fa57a))
* Adiciona Dockerfile para construção e execução da aplicação ([02fbbad](https://github.com/CesarAVB/bookstream-service/commit/02fbbad6395080c4ba87275dd1e59aad3e1f4503))
* Adiciona manipulador global de exceções para tratamento de erros ([7873ed0](https://github.com/CesarAVB/bookstream-service/commit/7873ed0c84d11ba431dc22488f3111dd1f26a626))
* Adiciona workflow para construção e publicação de imagens Docker ([5a4dbc6](https://github.com/CesarAVB/bookstream-service/commit/5a4dbc6278307d4e36f0e9a4ab368c614272bcb0))

## [1.1.0](https://github.com/CesarAVB/bookstream-service/compare/v1.0.0...v1.1.0) (2026-02-23)

### Features

* Adiciona controlador LivroController com endpoints para ([9b576ad](https://github.com/CesarAVB/bookstream-service/commit/9b576ad5b275f2fc9317d652af32816bd44221de))

## 1.0.0 (2026-02-23)

### Features

* Adiciona configuração do MinIO para integração com S3 ([17861f6](https://github.com/CesarAVB/bookstream-service/commit/17861f6cab21b323cccfebc3b0f684c24ea68317))
* Adiciona configuração para release automática com semantic-release ([e0df54d](https://github.com/CesarAVB/bookstream-service/commit/e0df54d15ab455bdd6da237afed13c13314d5506))
* Adiciona configurações de ambiente para produção, incluindo MinIO ([d1c90af](https://github.com/CesarAVB/bookstream-service/commit/d1c90afc7504e70d952cbd19cacccf069e2f5371))
* Adiciona DTOs para cadastro, atualização e filtragem de livros ([3f6e55b](https://github.com/CesarAVB/bookstream-service/commit/3f6e55bdbfabb9f4ec98565106068b5b66eab3fc))
* Adiciona entidade Livro com atributos e anotações JPA ([2b0d444](https://github.com/CesarAVB/bookstream-service/commit/2b0d444fdc2039349a0887926eb79cf6b38270d4))
* Adiciona enums StatusLivro e TipoLivro para gerenciamento de ([0db1835](https://github.com/CesarAVB/bookstream-service/commit/0db18357ff9f6d1b98843f64a75b86f0c7bd6a60))
* Adiciona repositório Livro com métodos de busca e atualização ([8708573](https://github.com/CesarAVB/bookstream-service/commit/8708573cb688d920aca8beedf1192888a45ebd38))
* Adiciona serviços LivroService e MinioService para gerenciamento ([fbbbb7a](https://github.com/CesarAVB/bookstream-service/commit/fbbbb7afcfee0980dc66b4d58978a54376e6d146))
