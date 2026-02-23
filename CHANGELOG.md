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
