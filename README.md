# Guidance

## Build & Deploy
Before run `./script.sh`, please target Java 21 with `export JAVA_HOME=` properly

## Test Setup
1. Create ssh tunnel `cf ssh -N -T -L 5000:localhost:5000 gateway`
2. Start jconsole(JDK tool), connct to localhost:5000
3. Monitor "MBeans > java.nio > BufferPool > direct > Attributes > MemoryUsed"

## Test
Send masssive HTTP requests to below endpoints parallelly and monitor direct buffer usage on jconsole/jvisualvm
- https://gateway.DOMAIN/users_25KB
- https://gateway.DOMAIN/users_25MB
- https://gateway.DOMAIN/bytes_25KB
- https://gateway.DOMAIN/bytes_25MB

The test client can be used to send async multiple requests

```
javac ConcurrentHttpsLoad.java
java ConcurrentHttpsLoad https://gateway.DOMAIN/bytes_25MB 50
```