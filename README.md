# Bencol App - Backend

Backend para la gestión operativa y administrativa de Bencol, desarrollado con Java y Spring Boot.

El sistema permite gestionar clientes, productos, precios especiales, ventas, inventario, envases reutilizables, proveedores, compras, cuentas por cobrar, cuentas por pagar, caja, egresos, usuarios, seguridad por roles y reportes.

---

## Tecnologías

- Java 17
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Data JPA
- Spring Security
- OAuth2 Resource Server
- JWT
- MySQL 8
- Flyway
- MapStruct
- Lombok
- Maven

---

## Requisitos

Para ejecutar el proyecto se necesita:

- Java 17 o superior
- MySQL 8
- Maven Wrapper incluido en el proyecto

Para verificar Java:

```bash
java -version
```

---

## Base de datos

El sistema utiliza MySQL.

El nombre utilizado para la base de datos es:

```text
bencol_system
```

Para desarrollo local puede crearse una base de datos vacía:

```sql
CREATE DATABASE bencol_system;
```

No es necesario crear manualmente las tablas.

El proyecto utiliza **Flyway** para administrar y versionar la estructura de la base de datos.

Las migraciones se encuentran en:

```text
src/main/resources/db/migration
```

Al iniciar la aplicación, Flyway verifica automáticamente la versión actual de la base de datos y ejecuta las migraciones pendientes.

Hibernate utiliza:

```yaml
ddl-auto: validate
```

Por lo tanto, Hibernate valida la estructura pero no crea ni modifica automáticamente las tablas.

La estructura de la base de datos debe ser administrada mediante Flyway.

---

## Perfiles de Spring

El sistema utiliza perfiles para separar la configuración de desarrollo y producción.

Los perfiles principales son:

```text
dev
prod
```

La configuración general se encuentra en:

```text
src/main/resources/application.yml
```

El perfil activo puede seleccionarse mediante la variable:

```text
SPRING_PROFILES_ACTIVE
```

Por defecto se utiliza:

```text
dev
```

---

## Desarrollo local

Para desarrollo se utiliza:

```text
application-dev.yml
```

Este perfil puede contener valores predeterminados exclusivamente para facilitar el desarrollo local.

Ejemplo:

```text
Base de datos: bencol_system
Servidor MySQL: localhost
Puerto aplicación: 8080
```

Las credenciales incluidas como valores predeterminados en desarrollo no deben utilizarse en producción.

---

## Producción

Para producción se debe utilizar:

```text
SPRING_PROFILES_ACTIVE=prod
```

El perfil de producción debe obtener las credenciales y secretos mediante variables de entorno.

### Variables requeridas

```text
DB_URL
DB_USERNAME
DB_PASSWORD

BENCOL_JWT_SECRET

BENCOL_ADMIN_USERNAME
BENCOL_ADMIN_EMAIL
BENCOL_ADMIN_PASSWORD
```

### Variables opcionales

```text
SERVER_PORT
JWT_EXPIRATION_MINUTES
LOG_FILE
```

Ejemplo conceptual de configuración en Windows CMD:

```cmd
set SPRING_PROFILES_ACTIVE=prod

set DB_URL=jdbc:mysql://localhost:3306/bencol_system?useSSL=false&serverTimezone=America/Lima&allowPublicKeyRetrieval=true
set DB_USERNAME=bencol_user
set DB_PASSWORD=CLAVE_SEGURA

set BENCOL_JWT_SECRET=SECRETO_JWT_LARGO_Y_SEGURO

set BENCOL_ADMIN_USERNAME=adminbencol
set BENCOL_ADMIN_EMAIL=admin@bencol.com
set BENCOL_ADMIN_PASSWORD=CLAVE_ADMIN_SEGURA
```

Luego puede ejecutarse el JAR:

```cmd
java -jar bencol-app-0.0.1-SNAPSHOT.jar
```

> Los valores anteriores son únicamente ejemplos. No deben utilizarse como credenciales reales de producción.

---

## Administrador inicial

Cuando el sistema se ejecuta por primera vez sobre una base de datos nueva, todavía no existen usuarios para iniciar sesión.

Para resolver este problema, el sistema contiene un inicializador:

```text
AdminInitializer
```

El inicializador verifica la cantidad de usuarios registrados.

Si ya existe al menos un usuario:

```text
usuarioRepository.count() > 0
```

no realiza ninguna acción.

Si no existe ningún usuario, intenta crear el administrador inicial utilizando:

```text
BENCOL_ADMIN_USERNAME
BENCOL_ADMIN_EMAIL
BENCOL_ADMIN_PASSWORD
```

El usuario creado recibe el rol:

```text
ADMIN
```

y el estado:

```text
ACTIVO
```

La contraseña no se almacena directamente.

Antes de guardarse es procesada mediante el `PasswordEncoder` configurado en Spring Security.

Después de crear el primer administrador, este puede iniciar sesión y administrar los demás usuarios del sistema.

### Importante

Las credenciales del administrador de producción no deben almacenarse directamente dentro del código fuente ni subirse al repositorio.

Deben configurarse mediante variables de entorno en el servidor.

---

## Autenticación

El sistema utiliza autenticación mediante JWT.

### Iniciar sesión

```http
POST /bencol.agua/auth/login
```

El login devuelve un token JWT cuando las credenciales son correctas.

Para acceder a endpoints protegidos se debe enviar:

```http
Authorization: Bearer TOKEN
```

### Usuario autenticado

```http
GET /bencol.agua/auth/me
```

Permite obtener información del usuario autenticado actualmente.

---

## Roles

El sistema maneja los siguientes roles:

```text
ADMIN
VENTAS
CAJA
COBRANZAS
ALMACEN
COMPRAS
```

### ADMIN

Posee acceso administrativo al sistema y permite gestionar todos los módulos autorizados por la configuración de seguridad.

### VENTAS

Orientado a operaciones comerciales.

Puede trabajar principalmente con:

- clientes
- productos
- precios especiales
- ventas
- envases
- reportes

### CAJA

Orientado a operaciones financieras de caja.

Puede trabajar principalmente con:

- caja
- movimientos
- egresos
- cuentas por cobrar
- cuentas por pagar
- pagos
- reportes

### COBRANZAS

Orientado al seguimiento y cobro de deudas de clientes.

Puede trabajar principalmente con:

- clientes
- ventas
- cuentas por cobrar
- pagos
- reportes

### ALMACEN

Orientado al control físico de productos.

Puede trabajar principalmente con:

- productos
- inventario
- envases
- consultas de compras
- reportes

### COMPRAS

Orientado a proveedores y abastecimiento.

Puede trabajar principalmente con:

- productos
- inventario
- proveedores
- compras
- cuentas por pagar
- pagos a proveedores
- reportes

---

# Módulos

## Clientes

Ruta base:

```text
/bencol.agua/clientes
```

Permite registrar, consultar y actualizar clientes.

---

## Productos

Ruta base:

```text
/bencol.agua/productos
```

Permite gestionar los productos comercializados por Bencol.

Los productos pueden representar diferentes presentaciones y paquetes comerciales.

---

## Precios especiales por cliente

Ruta base:

```text
/bencol.agua/clientes-precios
```

Permite establecer precios personalizados para combinaciones específicas de cliente y producto.

---

## Ventas

Ruta base:

```text
/bencol.agua/ventas
```

Las ventas pueden contener múltiples detalles.

Durante el procesamiento de una venta el sistema puede:

1. validar el cliente;
2. validar los productos;
3. determinar el precio correspondiente;
4. registrar la venta y sus detalles;
5. actualizar el inventario;
6. procesar operaciones relacionadas con envases;
7. generar la correspondiente cuenta por cobrar.

Las ventas anuladas son consideradas por las reglas correspondientes del sistema y los reportes excluyen las operaciones anuladas cuando corresponde.

---

## Inventario

Ruta base:

```text
/bencol.agua/inventarios
```

Permite controlar las existencias de los productos.

El inventario mantiene información como:

```text
stock actual
stock mínimo
stock máximo
```

Las operaciones comerciales pueden generar movimientos de entrada o salida.

Por ejemplo:

```text
Compra
    ↓
Entrada de inventario

Venta
    ↓
Salida de inventario
```

Para productos comercializados en paquetes, el sistema considera las unidades físicas correspondientes al paquete.

Ejemplo:

```text
10 paquetes
×
6 unidades por paquete
=
60 unidades físicas
```

---

## Envases reutilizables

Ruta base:

```text
/bencol.agua/envases
```

El sistema contiene lógica específica para controlar los envases reutilizables.

Entre los movimientos manejados se encuentran:

```text
INTERCAMBIO
PRESTAMO
COMPRA
DEVOLUCION
CONVERSION_COMPRA
AJUSTE
```

El saldo inicial permite registrar la situación real de un cliente que ya poseía envases antes de incorporarse al sistema.

Por seguridad, el registro de saldo inicial está restringido al rol:

```text
ADMIN
```

---

## Cuentas por cobrar

Ruta base:

```text
/bencol.agua/finanzas/cuentas
```

Permite controlar las deudas generadas por ventas.

Los estados utilizados incluyen:

```text
PENDIENTE
PARCIAL
PAGADA
ANULADA
```

Los pagos pueden ser parciales o completos.

Cada pago actualiza:

```text
monto pagado
saldo pendiente
estado de la cuenta
```

Cuando corresponde, los pagos recibidos generan movimientos de ingreso en caja.

---

## Proveedores

Ruta base:

```text
/bencol.agua/proveedores
```

Permite registrar, consultar y actualizar proveedores.

---

## Compras

Ruta base:

```text
/bencol.agua/compras
```

El flujo general de una compra es:

```text
Compra
    ↓
Detalle de compra
    ↓
Actualización de inventario
    ↓
Cuenta por pagar
```

Las compras anuladas son excluidas de los cálculos correspondientes cuando aplica.

---

## Cuentas por pagar

Ruta base:

```text
/bencol.agua/finanzas/cuentas-pagar
```

Permite controlar las obligaciones pendientes con proveedores.

Estados:

```text
PENDIENTE
PARCIAL
PAGADA
ANULADA
```

El sistema admite pagos parciales y completos.

Cuando se registra un pago a proveedor, este puede generar automáticamente un movimiento de egreso en caja.

---

## Caja

Ruta base:

```text
/bencol.agua/caja
```

Permite controlar la caja operativa.

Entre sus operaciones principales se encuentran:

```text
abrir caja
consultar caja
registrar movimientos
consultar movimientos
cerrar caja
```

La caja mantiene información como:

```text
saldo inicial
total ingresos
total egresos
saldo actual
saldo esperado
saldo real
diferencia
```

El cálculo general es:

```text
saldo esperado =
saldo inicial + total ingresos - total egresos
```

Al cerrar la caja:

```text
diferencia =
saldo real - saldo esperado
```

Los movimientos pueden ser:

```text
INGRESO
EGRESO
```

---

## Egresos

Ruta base:

```text
/bencol.agua/egresos
```

Permite registrar gastos operativos.

Cada egreso contiene información como:

```text
categoría
concepto
monto
referencia
usuario que realizó el registro
fecha
```

Los egresos registrados afectan la caja correspondiente.

---

# Reportes y dashboard

Ruta base:

```text
/bencol.agua/reportes
```

El sistema proporciona diferentes endpoints para análisis operativo y financiero.

### Dashboard general

```http
GET /bencol.agua/reportes/dashboard
```

Resume información de:

```text
ventas
finanzas
caja
compras
inventario
```

### Productos más vendidos

```http
GET /bencol.agua/reportes/productos-mas-vendidos
```

### Ventas por día

```http
GET /bencol.agua/reportes/ventas-por-dia
```

### Clientes con mayor deuda

```http
GET /bencol.agua/reportes/clientes-mayor-deuda
```

### Productos con stock bajo

```http
GET /bencol.agua/reportes/stock-bajo
```

### Ventas por mes

```http
GET /bencol.agua/reportes/ventas-por-mes
```

### Ingresos por mes

```http
GET /bencol.agua/reportes/ingresos-por-mes
```

### Egresos por mes

```http
GET /bencol.agua/reportes/egresos-por-mes
```

### Flujo neto por mes

```http
GET /bencol.agua/reportes/flujo-neto-por-mes
```

El flujo neto se calcula como:

```text
flujo neto =
ingresos - egresos
```

### Tendencias

```http
GET /bencol.agua/reportes/tendencias
```

Compara el período solicitado con el período inmediatamente anterior de la misma duración.

Incluye comparaciones de:

```text
ventas
ingresos
egresos
flujo neto
```

### Rentabilidad de productos

```http
GET /bencol.agua/reportes/rentabilidad-productos
```

Permite obtener información estimada de:

```text
cantidad vendida
unidades físicas vendidas
ventas
costo promedio
costo estimado
margen estimado
margen porcentual
```

---

# Seguridad

El backend utiliza Spring Security y JWT.

Las rutas están protegidas según el rol del usuario.

El servidor utiliza:

```text
SessionCreationPolicy.STATELESS
```

por lo que no mantiene sesiones HTTP tradicionales.

Cada solicitud protegida debe contener un JWT válido.

---

# Manejo de errores

El backend dispone de manejo centralizado de excepciones.

Las respuestas de error pueden contener un:

```text
requestId
```

que permite relacionar el error recibido por el cliente con los logs del servidor.

Esto facilita el diagnóstico de problemas en producción.

---

# Logs

Por defecto los logs se almacenan en:

```text
logs/bencol-app.log
```

El nivel de logging depende del perfil utilizado.

En desarrollo se puede utilizar mayor detalle para depuración.

En producción se reducen los logs SQL y otros mensajes internos para evitar ruido innecesario.

---

# Compilar el proyecto

En Windows:

```cmd
mvnw.cmd clean package
```

En Linux/macOS:

```bash
./mvnw clean package
```

Si la compilación termina correctamente debe aparecer:

```text
BUILD SUCCESS
```

El JAR generado se encuentra en:

```text
target/bencol-app-0.0.1-SNAPSHOT.jar
```

---

# Ejecutar el proyecto

## Desarrollo

Puede ejecutarse desde IntelliJ IDEA o mediante Maven:

```cmd
mvnw.cmd spring-boot:run
```

---

## Ejecutar el JAR

Después de compilar:

```cmd
java -jar target/bencol-app-0.0.1-SNAPSHOT.jar
```

---

## Ejecutar en producción

Configurar:

```text
SPRING_PROFILES_ACTIVE=prod
```

junto con las variables de entorno correspondientes.

Posteriormente ejecutar:

```cmd
java -jar target/bencol-app-0.0.1-SNAPSHOT.jar
```

---

# Flujo general del sistema

Uno de los principales flujos comerciales es:

```text
Cliente
   ↓
Venta
   ↓
Inventario
   ↓
Cuenta por cobrar
   ↓
Pago
   ↓
Ingreso de caja
```

El flujo de abastecimiento es:

```text
Proveedor
   ↓
Compra
   ↓
Inventario
   ↓
Cuenta por pagar
   ↓
Pago a proveedor
   ↓
Egreso de caja
```

Los gastos operativos siguen:

```text
Egreso
   ↓
Movimiento de caja
   ↓
Actualización del saldo
```

---

# Verificación del proyecto

El proyecto puede verificarse mediante:

```cmd
mvnw.cmd clean package
```

La compilación debe completar las fases de:

```text
compile
testCompile
test
jar
spring-boot:repackage
```

Un resultado correcto debe finalizar con:

```text
BUILD SUCCESS
```

---

# Consideraciones de seguridad

Nunca deben subirse al repositorio credenciales reales de producción.

Especialmente:

```text
DB_PASSWORD
BENCOL_JWT_SECRET
BENCOL_ADMIN_PASSWORD
```

Estas credenciales deben configurarse mediante variables de entorno.

Tampoco deben incluirse en el repositorio:

```text
logs/
target/
archivos de configuración del IDE
archivos .env con secretos reales
```

---

# Despliegue inicial

Para instalar el sistema en un entorno nuevo:

1. Instalar Java y MySQL.
2. Crear una base de datos vacía.
3. Configurar las variables de entorno.
4. Configurar `SPRING_PROFILES_ACTIVE=prod`.
5. Ejecutar el JAR.
6. Flyway creará la estructura de la base de datos mediante las migraciones.
7. Si no existen usuarios, `AdminInitializer` creará el administrador inicial utilizando las variables de entorno configuradas.
8. Iniciar sesión con el administrador inicial.
9. Crear los usuarios correspondientes para cada área.
10. Comenzar a utilizar el sistema.

---

# Estado del proyecto

Backend funcional con los principales módulos integrados:

- clientes
- productos
- precios especiales
- ventas
- inventario
- envases
- cuentas por cobrar
- proveedores
- compras
- cuentas por pagar
- caja
- egresos
- usuarios
- autenticación JWT
- autorización por roles
- reportes
- dashboard

El proyecto utiliza migraciones de base de datos mediante Flyway y puede ser empaquetado como un JAR ejecutable de Spring Boot.

---

# Autor

RUNICSOFT

Desarrollado con Java y Spring Boot.