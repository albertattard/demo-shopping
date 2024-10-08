[//]: # (Automatically generated by Sociable Weaver)
# Shopping Modular Monolithic Spring Boot Demo

A simple monolithic application that has two components.

- Catalogue
- Cart (shopping cart)

The _Cart_ component depends on information available in the _Catalogue_
component, such as the _CatalogueItem_ _id_ and _caption_.

Both components are deployed as a single Spring Boot web application.

This application uses the modular monolithic architecture style where the
classes are grouped based on their component as shown next.

```shell
tree --charset=ascii --dirsfirst --sort=name -L 1 './src/main/java/demo'
```

The top level packages

```
./src/main/java/demo
|-- cart
|-- catalogue
|-- common
`-- Main.java

4 directories, 1 file
```

Domain partitioning organises the code by the domain it belongs to.

By looking at the top-level packages, we can discern what this application is
about. The top-level package reflects the business language too. For instance,
if we want to add an image to the catalogue items, which package should we work
in? That’s not a tricky question – it’s the `demo.catalogue` package. Similarly,
if we need to allow the user to remove items from the shopping cart, where
should we look? Look no further than the `demo.cart` package.

This simplifies the application and earns this architectural style. Many believe
that this architecture style achieves a good balance as it brings in the
advantages of smaller services without the complexity of distributed
applications.

## Preloaded data

To keep things simple, we’ll use the
[H2](https://www.h2database.com/html/main.html) in-memory database preloaded
with the following data:

- **Catalog Items**

  | `id` | `caption`     | `description`                                                         |
  | ---: | ------------- | --------------------------------------------------------------------- |
  |    1 | Leather Sofa  | A very nice and comfortable sofa                                      |
  |    2 | Wooden Table  | A large table ideal for 6 to 8 people                                 |
  |    3 | Plastic Chair | A robust plastic chair ideal for children and adults alike            |
  |    4 | Mug           | The ideal way to start the day                                        |
  |    5 | LED TV        | A very large TV set, ideal for those who love to binge-watch TV shows |

- **Carts**

  | `id` |
  | ---: |
  |    1 |
  |    2 |
  |    3 |

- **Cart Items**

  | `cart_id` | `item_id` | `quantity` |
  | --------: | --------: | ---------: |
  |         1 |         1 |          1 |
  |         1 |         5 |          1 |
  |         2 |         2 |          1 |
  |         2 |         3 |          6 |
  |         3 |         4 |          4 |

The `cart_id` and `item_id` in the cart items table are foreign keys to the cart
and catalogue item tables, respectively.

## Prerequisites

- [Oracle Java 21](https://www.oracle.com/java/technologies/downloads/#java21)

## Run the example

1. **Build the application**

   ```shell
   ../mvnw clean package
   ```

   All tests should pass.

   (_Optional_) Verify that the fat JAR files were created.

   ```shell
   tree --charset=ascii --dirsfirst --sort=name -L 1 --prune './target'
   ```

   The fat and thin JAR files

   ```
   ./target
   |-- demo-shopping-monolithic-modular-1.0.0.jar
   `-- demo-shopping-monolithic-modular-1.0.0.jar.original

   1 directory, 2 files
   ```

2. **Run the applications**

   This application doesn’t have any dependencies and listens to web requests on
   port `8080` unless otherwise specified.

   ```shell
   # Start the application in the background
   java -jar './target/demo-shopping-monolithic-modular-1.0.0.jar' > './target/output.txt' 2>&1 &

   # Wait for the application to start
   while [ "$(curl --silent --output /dev/null --write-out '%{http_code}' 'http://localhost:8080/catalogue/item/1')" -ne '200' ]
   do
     echo 'Waiting for the application to start'
     sleep 1
   done
   ```

   This starts our application in the background and waits for it to start.

3. **Request a catalogue item**

   The database contains the following catalogue items, as shown in the
   following table.

   | `id` | `caption`     | `description`                                                         |
   | ---: | ------------- | --------------------------------------------------------------------- |
   |    1 | Leather Sofa  | A very nice and comfortable sofa                                      |
   |    2 | Wooden Table  | A large table ideal for 6 to 8 people                                 |
   |    3 | Plastic Chair | A robust plastic chair ideal for children and adults alike            |
   |    4 | Mug           | The ideal way to start the day                                        |
   |    5 | LED TV        | A very large TV set, ideal for those who love to binge-watch TV shows |

   ```shell
   curl --silent 'http://localhost:8080/catalogue/item/1' | jq
   ```

   The catalogue item with id `1`

   ```json
   {
     "id": 1,
     "caption": "Leather Sofa",
     "description": "A very nice and comfortable sofa"
   }
   ```

4. **Request a cart**

   The database also contains the following carts, with their respective
   catalogue items and quantities, as shown in the following table.

   | `cart_id` | `item_id` | `caption`     | `quantity` |
   | --------: | --------: | ------------- | ---------: |
   |         1 |         1 | Leather Sofa  |          1 |
   |         1 |         5 | LED TV        |          1 |
   |         2 |         2 | Wooden Table  |          1 |
   |         2 |         3 | Plastic Chair |          6 |
   |         3 |         4 | Mug           |          4 |

   ```shell
   curl --silent 'http://localhost:8080/cart/1' | jq
   ```

   The cart item with id `1`

   ```json
   {
     "id": 1,
     "items": [
       {
         "id": 1,
         "caption": "Leather Sofa",
         "quantity": 1
       },
       {
         "id": 5,
         "caption": "LED TV",
         "quantity": 1
       }
     ]
   }
   ```

5. **Add a new catalogue item**

   Add the following item to the catalogue

   | Property Name | Value                                   |
   | ------------- | --------------------------------------- |
   | `caption`     | Green Plant                             |
   | `description` | Put a little life in your living room!! |

   ```shell
   # Save the response in a file as we will use it later on
   curl --silent \
     -X POST 'http://localhost:8080/catalogue/item' \
     -H 'Content-Type: application/json' \
     -d '{"caption":"Green Plant","description":"Put a little life in your living room!!"}' \
     | jq \
     > './target/new-catalogue-item.json'

   # Print the output
   jq . './target/new-catalogue-item.json'
   ```

   This will return the newly added catalogue item together with its id.

   ```json
   {
     "id": 6,
     "caption": "Green Plant",
     "description": "Put a little life in your living room!!"
   }
   ```

   Print the new catalogue item id

   ```shell
   # Print the new catalogue item id
   ITEM_ID="$(jq .id './target/new-catalogue-item.json')"
   echo "New catalogue item id: ${ITEM_ID}"
   ```

   The newly created catalogue item id

   ```
   New catalogue item id: 6
   ```

6. **Add the new catalogue item to cart**

   Please note that this depends on the previous step.

   Fetch the cart details.

   ```shell
   curl --silent 'http://localhost:8080/cart/3' | jq
   ```

   The current cart contents

   ```json
   {
     "id": 3,
     "items": [
       {
         "id": 4,
         "caption": "Mug",
         "quantity": 4
       }
     ]
   }
   ```

   Read the new item id obtained from the previous step and fetch the catalogue
   item.

   ```shell
   # Read the new item id obtained from the previous command
   ITEM_ID="$(jq .id './target/new-catalogue-item.json')"

   # Fetch the new catalogue item
   curl --silent "http://localhost:8080/catalogue/item/${ITEM_ID}" | jq
   ```

   The newly created catalogue item

   ```json
   {
     "id": 6,
     "caption": "Green Plant",
     "description": "Put a little life in your living room!!"
   }
   ```

   Add the newly created catalogue item to the cart.

   ```shell
   # Read the new item id obtained from the previous command
   ITEM_ID="$(jq .id './target/new-catalogue-item.json')"

   # Add the new item to cart with id 3
   curl --silent -X POST "http://localhost:8080/cart/3/item/${ITEM_ID}" | jq
   ```

   The cart contents after adding the newly created catalogue item.

   ```json
   {
     "id": 3,
     "items": [
       {
         "id": 4,
         "caption": "Mug",
         "quantity": 4
       },
       {
         "id": 6,
         "caption": "Green Plant",
         "quantity": 1
       }
     ]
   }
   ```

7. **Stop the application once ready**

   ```shell
   kill "$(jcmd | grep 'demo-shopping-monolithic-modular-1.0.0.jar' | cut -d' ' -f1)"
   ```
