# Guidance

## Build & Deploy
Before run `./script.sh`, please target Java 21 by `export JAVA_HOME=` properly

## Test Setup
1. Install JVisualVM
2. Install Buffer Pool plugin
3. Create ssh tunnel `cf ssh -N -T -L 5000:localhost:5000 gateway`
4. Add a JMX connect with "localhost:5000"

## Test
Send masssive HTTP requests to below endpoints parallelly and monitor direct buffer usage on JVisualVM
- https://gateway.DOMAIN/users_25KB
- https://gateway.DOMAIN/users_25MB
- https://gateway.DOMAIN/bytes_25KB
- https://gateway.DOMAIN/bytes_25MB