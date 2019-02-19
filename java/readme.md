## Dependencies

As described [here](https://www.rabbitmq.com/tutorials/tutorial-one-java.html)

## Running from the shell

### PubSub Topic

```
java -cp $cp  pubsub_topic.receivelogstopic "#" # all msgs
java -cp $cp  pubsub_topic.receivelogstopic "*.info" # any source but only info severity
java -cp $cp  pubsub_topic.receivelogstopic "kern.*" # only kern source but all severities
java -cp $cp pubsub_topic/emitlogtopic kern info some log msg # send for kern source with info seveirty
```

### RPC

```
java -cp $CP  rpc/Server
java -cp $CP rpc/Client 9
```

### Work Queue

```
for i in `seq 1 10`; do java -cp $CP work_queue.NewTask $i:Long.....; sleep 5; done
for i in `seq 1 50`; java -cp $CP work_queue.NewTask $i:Short.; sleep 1 done;
```