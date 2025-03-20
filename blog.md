Task 1).

Analysis of engineering requirements:

The fundemental premise here is that we need to implement the ability for producer and consumer entities to share data asynchronously via a stream-like channel, where data/events can be produced and consumed independently. This is done by having a tributary cluster, which contains a series of topics (each with a generic type). These topics in turn contain partitions which act as queues for messages -> these also allow for parallel processing. Messages also have their own structure with id, key, value, e.t.c.

Two different types of producers, random and manual, and then consumers process these messages.

There needs to also be the functionality for consumer rebalancing in the range and round-robin strategy as well as the ability to replay messages from a specific offset.

This is all controlled through a command line interface that interacts with the system enabling CRUD operations.

Usability tests:

- Show that a topic can be created through CLI
- Show that partition can be created through CLI
- Show that consumer groups and consumers are created through CLI
- Show that consumers can be deleted from the consumer group through CLI
- Show that producers can be created through CLI
- Show that producers can produce events for partitions through CLI
- Show that consumers can consume events from partitions through CLI
- Show that show topic and show consumer functionality works through CLI
- Show that it is possible to do parallel production and consumption
- Show that rebalancing works and confirm this through output in CLI
- Show that playback works and confirm this through output in CLI

Initial Java API Design:

The idea here is just to use interfaces in the API, are then implemented in the core code. This allows the actual implementation to be abstracted away, while being able to use the functionality through the API.

UML Diagram:

Is in this folder as a pdf labelled Ass 3 Task 1 UML

Testing Plan:

Component tests on

- Consumer
- Producer
- Message
- Partition
- Producer
- Topic
- Tributary Cluster

Integration tests where

- Create consumers, partitions and perform a produce for the partition
- Create consumers and partitions and perform a consume for the partition
- Create consumers and partitions with integer and perform parallel produce
- Create consumers and partitions with integer and perform parallel consume
- Create consumers and partitions with integer and perform consume, then replay back
- Create consumers and partitions with integer and then check rebalancing occurs when adding new partitions

Implementing the solution:
Likely using a component-driven approach, and this will be easier to manage and write tests for, and easier to collaborate with my partner (ended up doing it solo though).

Task 2).

Youtube video link is here:

https://www.youtube.com/watch?v=pqjXeRPHXrE&feature=youtu.be

Task 3).

Reflection:

Final testing plan:

Unit tests

- Topic and Partition component tests
- Consumer, Consumer Group and Consumer Group Manager component tests
- producer and producer manager compomnent tests
- Messages unit tests

Integration tests

- test producing event with random allocation
- test producing event with manual allocation
- test consuming event and consuming multiple events
- test parallel producing and parallel consumption

Final list of usability tests:
Input these commands into the CLI:

invalid command
create
create topic
create topic topic1
create topic topic1 String
create partition topic1 partition1
create partition topic1 partition1 partition
show topic topic1
create producer producer1 Double Random
create producer producer1 String Random
produce event producer1 topic1 app/src/test/java/tributary/eventJSONs/event1.json
produce event producer1 topic1 app/src/test/java/tributary/eventJSONs/event1.json
produce event producer1 topic1 app/src/test/java/tributary/eventJSONs/event2.json
produce event producer1 topic1 app/src/test/java/tributary/eventJSONs/event3.json
show topic topic1
create consumer group group1 topic1 Range
create consumer group1 consumer1
create consumer group1 consumer2
show consumer group group1
delete consumer consumer2
delete consumer consumer3
show consumer group group1
create consumer group1 consumer2
show consumer group group1

create partition topic1 partition2
show consumer group group1
show topic topic1
consume event consumer1 partition1
consume event consumer1 partition2
consume event consumer1 partition1 4

parallel produce producer1 topic1 app/src/test/java/tributary/eventJSONs/event1.json producer1 topic1 app/src/test/java/tributary/eventJSONs/event2.json producer1 topic1 app/src/test/java/tributary/eventJSONs/event3.json producer1 topic1 app/src/test/java/tributary/eventJSONs/event4.json

show topic topic1

parallel consume consumer1 _ consumer2 _ (depends on what partitions are assigned)

show topic topic1

Task 3 final UML:

Is attached in the directory

Design Choices:

Design patterns used were:

- Strategy pattern (not implemented but planned) - for the rebalancing strategies as these can be changed at runtime
- Observer pattern for the topic and consumer groups - whenever a partition is added to a topic, it must notify all the consumer groups atttached to it, so rebalancing can occur - so topic is the publisher and consumer groups are subscribed
- Singleton pattern to ensure there exists only one tributary cluster that can be modified at a time, as well as one single consumer group manager and producer manager.

Design considerations in the solution -> I used interfaces to abstract away the code so I could separate out an API and the core code.
I adhered to LSP and LoD by ensuring each class and interface could be substituted without affecting the program's functionality and by minimizing the dependencies between objects, respectively.
By not storing the tributary cluster, conusmer group and producer info in the tributaryCLI as well, I minimised coupling between the classes.
-> Also used generics. This is so that we can simply modify the types of messages supported in the CLI factory, while it follows open close everywhere else, ensuring type safety as well.
-> Used threads also for concurrency, ensuring that there is correctness as well through thorough testing.

Reflection:

This assignment took much longer than expected, and I also came across many challenges. Similar to assignment 3, I had to design the system from scratch and it was very difficult coming up with a system that adhered to good design principles. This meant I had to change my design several times and add new classes, as well as try to figure out how to do the synchronised java stuff. I think from this assignment I learned a lot more about concurrency, especially in java, as well as the challenge of defensive programming in building the CLI - as I had to sanitise inputs and this took a lot of code. As my partner ended up not contributing to the assignment, I ended up going with a feature driven approach, as this was easier to track progress with was easy to incorporate with just myself working on it.
