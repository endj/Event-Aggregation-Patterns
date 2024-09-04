## Build:
```sh
./message_handler/gradlew -p message_handler clean build
cargo build --manifest-path message_consumer/Cargo.toml
```

## Run

### Start Docker

```sh
docker-compose -f docker/docker-compose.yaml up -d
```
### Start Handler
```sh
./message_handler/gradlew -p message_handler run   
```
### Start Consumer
```sh
cargo run --manifest-path message_consumer/Cargo.toml
```

## Test

```sh
python3 test_suite/basic_test.py
python3 test_suite/user_partition_test.py
```

## Architecture (WIP)

![design](architecture-wip.png)
