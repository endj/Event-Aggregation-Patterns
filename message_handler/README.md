# Build:
```
./build.sh
```

# Run

### Start Docker

```sh
docker-compose -f docker/docker-compose.yaml up -d
```
### Start Dispatcher
```sh
./message_dispatcher/gradlew -p message_dispatcher run   
```
### Start Consumer
```sh
cargo run --manifest-path message_consumer/Cargo.toml
```
