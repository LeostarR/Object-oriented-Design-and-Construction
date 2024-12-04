### Introduction

The basic training objective of the second unit assignment is to simulate a **multi-threaded real-time elevator system**, with three assignments for iterative development.

- The first assignment requires designing an elevator system with functions such as moving up and down, opening and closing doors, and simulating passenger entry and exit. The goal is to familiarize us with thread creation, operation, and the design methods of multi-threaded programs.
- The second assignment adds new features for scheduling passengers during elevator system expansion and daily maintenance. The aim is to master thread safety knowledge and solve thread safety issues while designing a hierarchical architecture around thread collaboration.
- The third assignment introduces scheduling parameters for the elevator system, specifically limiting the number of elevators that can be in an open state on each floor at any given time, and acknowledging that not all elevators may reach every floor. The goal is to master interaction between threads and reinforce the design of collaborative hierarchies among threads.

You can refer to my code here: [GitHub Repository](https://github.com/LeostarR/Object-oriented-Design-and-Construction)

### Summary of First Assignment

#### Design and Architecture

This assignment required completing the following tasks:

1. Design a **Queue** class for adding/removing passengers, along with methods to check if it's empty or finished, and to set its end status.
2. Design a **Controller** class as a bridge between the main waiting queue and elevators, responsible for passenger allocation.
3. Design an **Elevator** class capable of performing the required tasks.
4. In `Main`, start the controller thread and elevator threads, send input (if any) to the main waiting queue, and set the queue to end when finished.

> Note: The course team has already provided interfaces for input and output, so parsing input requests and formatting timestamp outputs are not considered.

#### Queue Class

##### `PassengerQueue`:

![Queue Class Diagram](https://pic.superbed.cc/item/66ab4496fcada11d3763b4c2.png)

`addPassenger(Passenger)` and `removePassenger(Passenger)` are used for adding and removing passengers, `getOnePassenger()` retrieves passengers in order of arrival, `setEnd(boolean)` sets the queue's end status. Other methods return attributes of the queue, such as whether it has ended, whether it's empty, and a collection (`ArrayList<Passenger>`) of all passengers.

#### Controller Class

##### `Controller`:

![Controller Class Diagram](https://pic.superbed.cc/item/66ab452ffcada11d3763c8b6.png)

The `run()` method was overridden to allocate passengers.

Allocation Strategy: A uniform distribution strategy was adopted. Passengers from the main waiting queue were evenly distributed to the waiting queues of elevators based on their arrival time. 

Example:

```java
while (true) {
    if (/*end condition*/) {
        // Set all elevator waiting queues to end//
        return;
    }
    Passenger passenger = waitQueue.getOnePassenger();//Retrieve in order of arrival//;
    if (passenger == null) {
        continue;
    }
    elevatorQueues.get(/*index*/).addPassenger(passenger);//Allocate
}
```

#### Elevator Class:

##### `Elevator`:

![Elevator Class Diagram](https://pic.superbed.cc/item/66ab4549fcada11d3763c9b2.png)

The `run()` method was overridden to operate the elevator. The general pattern is to determine the main request based on the order in the waiting queue and deliver the main request passenger to their destination. This process is divided into two parts: picking up the main request passenger and delivering them. During these processes, if the elevator is not overloaded (including the main request) and moving in the same direction, it can carry additional passengers. When each main request arrives, the earliest passenger inside the elevator becomes the main request, and continues until the elevator is empty, then fetches more passengers from the waiting queue.

```java
@Override
public void run() {
    while (true) {
        if (/*end condition*/) {
            return;
        }
        Passenger passenger = queue.getOnePassenger();//queue(ArrayList<Passenger>) is this elevator's waiting queue
        if (passenger == null) {
            continue;
        }
        // Two steps:
        // 1. Set the earliest passenger's starting point as the main request, pick up
        this.setTarget(passenger.getFromFloor());
        forward(passenger);
        // 2. Set the passenger's destination as the main request, drop off
        this.setTarget(passenger.getToFloor());
        forward(passenger);
        while (!this.list.isEmpty()) {
            // Deliver remaining passengers one by one
        }
    }
}
```

The `forward(Passenger p)` method is used for picking up or dropping off passenger `p`, while also allowing for piggybacking under certain conditions:

```java
private void forward(Passenger p) {
    while (this.nowFloor != this.targetFloor) { //Piggyback: same direction, target less than main target
        if (/*piggyback or intermediate disembarkation conditions are met*/openOrNot()) {
            // Open door, disembark, embark, close door
        }
        this.move();//Move one floor
    }
    // Reach the designated floor
    this.open();
    // 1. If p is inside the elevator and has arrived at the destination, let p disembark first
    // 2. If piggyback passengers' destinations are also on this floor, disembark
    // 3. If p hasn't boarded the elevator yet, let p board first
    // 4. Based on remaining capacity, piggyback passengers who meet the conditions on this floor
    this.close();
}
```

Methods like `in()`, `out()`, `open()`, `close()`, and `arrive()` require specific outputs, while `addPassenger()` and `removePassenger()` manage the addition and removal of passengers within the elevator queue (these methods differ from those in the previous queue class because each elevator's internal queue is only operated by the current elevator, so thread safety is not an issue), which are relatively straightforward to implement.

#### Interaction Between Threads and Overall Workflow

Starting with input, the official interface accepts input, and `Main` sends each `Passenger` to the main waiting queue `waitQueue` (equivalent to the tray in the producer-consumer model). The controller acts as a bridge between elevators and passengers, allocating passengers from `waitQueue` to different elevator waiting queues according to a certain method. At this point, the relationship between elevators and waiting queues corresponds to consumers and trays, except that one waiting queue corresponds to only one elevator. Therefore, each elevator only needs to ensure that all passengers in its waiting queue are processed until it receives an end signal and both the waiting queue and the elevator are empty, at which point the thread ends.

#### Synchronized Blocks and Locks

Unlike ordinary programs, multithreaded programming must pay attention to thread safety issues. For example, if two threads simultaneously perform write operations on an object, conflicts and exceptions are likely to occur. To ensure that the object is accessed by only one thread at a time, the `synchronized` keyword can be added to all member methods that might be accessed by multiple objects, and `notifyAll()` can be called at the end of the method to wake up other threads. When a thread enters a synchronized block, it automatically acquires the lock object associated with the synchronized block, and when it exits the synchronized block, it automatically releases the lock object, ensuring that the processing statements within the synchronized block are not interfered with by other threads. For instance:

```java
public synchronized void removePassenger(Passenger p) {
    //Your Method
    notifyAll();
}
```

#### UML Class Diagram

![UML Class Diagram](https://pic.superbed.cc/item/66ab4564fcada11d3763cb0b.png)

#### Bug Analysis

During the mid-term test ~~before writing the controller~~ frequent timeouts occurred, but after adding the controller, there were no bugs.

In the strong test and mutual test, there was an unstable bug that could not be reliably reproduced, which was most likely caused by high-frequency issuance leading to certain problems, resulting in passengers not entering or exiting, causing the program to fail to terminate. Upon checking the code, I found that the actual elevator capacity set for piggybacking passengers was 5 (@_@), correcting this solved the problem.

## Summary of Second Assignment

### Design and Architecture

Based on the first assignment, the following work needed to be added:

1. Add attribute variables of the **Elevator** class to the constructor to support personalized addition of elevators, replacing constants with the properties of the elevator in the implementation.
2. Add methods and variables related to `maintain` in the **Queue** class to meet maintenance needs.
3. Add methods in the **Elevator** class's running method to judge whether `maintain` is true and handle measures accordingly.
4. Modify the condition for thread termination (`waitQueue` now has sources other than `main`, including elevators under maintenance).

#### Queue Class

##### `PassengerQueue`:

![Queue Class Diagram](https://pic.superbed.cc/item/66ab457ffcada11d3763cc5f.png)

Added `setMaintainSymbol(int)`, `isMaintained()`, and `addMaintainList(ArrayList<PersonRequest>)`. `setMaintainSymbol(int)` and `isMaintained()` are used to set and check the maintenance flag, while `addMaintainList()` adds all elements of parameter `ArrayList<PersonRequest> list` to the `List` of this object, adapting to the operation of adding personnel related to maintenance elevators to `waitQueue`.

#### Controller Class

##### `Distribute`:

~~Yes, it's renamed~~

Since elevators can also add passengers to `waitQueue`, the termination condition of the controller needs to be modified:

```java
while (true) {
    if (waitQueue.isEnd() && waitQueue.isEmpty()) {
        try {
            sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (!waitQueue.isEmpty()) {
            continue;
        }
        for (PassengerQueue elevatorQueue : elevatorQueues) {
            elevatorQueue.setEnd(true);
        }
        return;
    }
    //Your Method
}
```

Compared to the previous version:

```java
while (true) {
    if (waitQueue.isEnd() && waitQueue.isEmpty()) {
        for (PassengerQueue elevatorQueue : elevatorQueues) {
            elevatorQueue.setEnd(true);
        }
        return;
    }
    //Your Method
}
```

Actually, only the middle part was added, considering that the process of `maintain` takes time. If the controller ends simply when `waitQueue` is empty, some elevators receiving `maintain` signals may not have enough time to release passengers, which could result in these passengers being unable to participate in allocation and never reaching their destination ~~_~~

So after satisfying the original termination condition, the controller sleeps for a fixed period. Since the course group requires that `maintain` operations must be completed within two floors of `arrive`, this time is not difficult to control. If it still satisfies the condition after sleeping, then it ends.

The dispatching strategy of the second assignment did not differ much from the first, so it will not be repeated here.

#### Elevator Class

##### `Elevator`:

A new method was added on top of the first assignment:

![Elevator Class Diagram](https://pic.superbed.cc/item/66ab459ffcada11d3763ce23.png)

Used to handle maintenance requests, an example (many details deleted, for reference only):

```java
private void dealMaintain(PersonRequest p) {
    ArrayList<PersonRequest> arr = new ArrayList<>(list);
    if (/*p is not in the elevator*/) {
       waitQueue.add(p);
    }
    for (PersonRequest person : arr) {
        if (/*passenger has not arrived*/) {
            // Change person's property fromFloor to the current floor
            person.setFromFloor(nowFloor);
            waitQueue.add(person);
        }
        this.out(person);
    }
    waitQueue.addAll(this.queue.getList());//Don't forget the poor people in the waiting queue :)
    this.maintainFlag = 1;
}
```

Also, new content was added in other methods like `forward()` and `run()` to adapt to maintenance needs:

```java
	while (this.nowFloor != this.targetFloor) { 
            // Need to judge maintain before normal boarding and alighting
           if (this.queue.isMaintained()) {
                this.dealMaintain(p);
                return;
            }
            // Original method
        }   

	@Override
    public void run() {
        while (true) {
            ......
            if (/*maintenance condition*/) {
                /*Output maintenance signal*/
                return;
            }
            ......
            forward(passenger);
               if (this.maintainFlag == 1) {
                continue;
            }
            while (/*elevator is not empty*/) {
                ......
                forward(passenger);
                if (this.maintainFlag == 1) {
                    break;
                }
            }
        }
```

Overall, it is implemented through changing `maintainFlag`.

#### Interaction Between Threads and Overall Workflow

The overall structure has not changed much compared to the first assignment. Differences include:

- The controller does not end immediately after standard input ends but waits until all maintenance elevators have been serviced.
- Elevator threads may end prematurely due to maintenance, by setting the `maintainSymbol` of the corresponding waiting queue, cutting off the connection between the controller and it, and then each elevator performs corresponding operations based on whether it received this `maintainSymbol`.

#### Synchronized Blocks and Locks

There are no significant differences from the first assignment.

#### UML Class Diagram

The second assignment used the official `PersonRequest` class, so it has one less class than the first assignment, with little difference.

![UML Class Diagram](https://pic.superbed.cc/item/66ab45b7fcada11d3763cff6.png)

#### Bug Analysis

This assignment was probably the most challenging of the unit, as my code had widespread CPU timeout issues, passing the mid-term test only after 9 submissions :(

On that Saturday, I spent the whole day in front of the computer without finding the problem, almost breaking down by night. Each submission resulted in 10 CPU timeouts, which was very distressing...

![Bug Image](https://pic.superbed.cc/item/66ab45d2fcada11d3763d135.png)

I sought help from teaching assistants and many classmates, and finally found the root cause of the problem:

At first, I did not make the controller sleep after all maintenance elevators completed and were allocated. Instead, I waited until all passengers left the entire system.

> Program input and output are **real-time interactive**, and the evaluation machine can achieve sending a certain amount of input at a certain time point.

That is, the end time of input should be near the moment when the last instruction is actually accepted. However, at that time, I misunderstood `setEnd` (my understanding deviation, thinking that the end of input refers to copying data to the terminal and pressing enter). After `setEnd`, the controller still had to wait for the last instruction because the last instruction might add passengers to `waitqueue`. So there was a large number of meaningless loops:

```java
Distribute.java:

        while (true) {
            ......
            if (waitQueue.isEnd()) {
                for (PassengerQueue elevatorQueue : elevatorQueues) {
                    elevatorQueue.setEnd(true);
                }
            }
            if (waitQueue.isEmpty() && waitQueue.isEnd() && table.isEmpty()) {
                return;
            }
            Passenger passenger = waitQueue.getOnePassenger();//Each result returned is null
            if (passenger == null) {                          
                continue;
            }
            .....
        }



PassengerQueue.java:

        public synchronized Passenger getOnePassenger() { //Retrieve in order of arrival (first come, first served)
            if (queue.isEmpty() && !this.isEnd()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (queue.isEmpty()) {
                return null;
            }
            ......
    	}
```

After setting `waitQueue` to `setEnd()`, the first `if` in `getOnePassenger` would be directly skipped each time, and since `queue` was indeed empty, it would return `null`. But the entire system still had passengers (`!table.isEmpty`), thus entering a large number of loops returning `null` and constantly `continue`, occupying a lot of `CPU` resources leading to timeouts. The solution is easy once you understand the timing of input ending, and the correct termination method has already been explained in the previous section of the summary of the second assignment -> Controller Class.

There were many piggybacking issues in the strong test, and I found that the piggybacking conditions were missing.

##### Debugging Methods

I used many tools to check CPU time, but none of them were substantially helpful. Printing something inside the loop is better; if the output of a method reaches hundreds of thousands of lines, it's easy to locate the problem:

```java
public void run() {
    while (true) {
        //System.out.println("**********distributeI");
        if (waitQueue.isEnd() && waitQueue.isEmpty()) {
            ......
        }
        ......
        //System.out.println("**********distributeO");
    }
}
```

By continuously bringing the two statements closer together, finding the branch with the most loops makes it easy to find the problem.

### Third Assignment Summary

#### Design and Architecture

Building upon the second assignment, the following new requirements were added:

1. Limit the number of elevators that can open their doors on each floor.
2. Consider the accessibility of some elevators, requiring path determination before passenger allocation.

To address these issues, I introduced a new `StateMap` class with various methods to solve the new problems.

#### Customized Requirement Class

##### `StateMap`



![StateMap Diagram](https://pic.superbed.cc/item/66ab45effcada11d3763d272.png)



For implementing the door opening limit, the solution is as follows:



![Service Map Diagram](https://pic.superbed.cc/item/66ab4606fcada11d3763d395.png)



The `value` in `serviceMap` corresponds to the number of elevators serving each floor, with the `key` indicating the floor level. Initially, the service count for each floor is set to 0.

When an elevator opens its doors, it calls `addService()`. Due to the `synchronized` keyword, only one elevator can access this method at a time. If the number exceeds the specified limit, the thread waits until the number of serving elevators on that floor falls below the limit, then increments the count:

java

深色版本



```
public synchronized void addService(int floor) {
    while (this.serviceMap.get(floor) >= 4) {
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    serviceMap.put(floor, serviceMap.get(floor) + 1);
    notifyAll();
}
```

Closing the door involves calling `removeService()`:

java

深色版本



```
public synchronized void removeService(int floor) {
    serviceMap.put(floor, serviceMap.get(floor) - 1);
    notifyAll();
}
```

For elevators that only pick up passengers, similar methods are implemented but require additional checks to determine if an elevator is designated for pickup only.

To resolve pathfinding issues, two `HashMap`s were defined:



![Maps Diagram](https://pic.superbed.cc/item/66ab4620fcada11d3763d4b8.png)



- `accessMap` indicates which floors each elevator can reach directly.
- `pathMap` indicates which floors can be reached directly from each floor (similar to an adjacency matrix).

A `findPath(Passenger p)` method returns a `LinkedList<Integer>` type demand queue, where the head is the passenger's next destination, and the tail is their final destination.

#### Distributor Class

##### `Distribute`:

During scheduling, the current demand floor and location floor of passengers must be reachable by the elevator, upon which they are evenly distributed:

java

深色版本



```
LinkedList<Integer> demand = stateMap.findPath(passenger);
passenger.updateDemand(demand);
do {
    i = (i + 1) % elevatorQueues.size();
} while (!(/*the current demand floor and location floor of the passenger are reachable by the elevator*/));
elevatorQueues.get(i).addPassenger(passenger);
```

#### Elevator Class

##### `Elevetor`:

Before opening and after closing the doors, the `stateMap` needs processing, and threads will `wait()` if conditions are not met until they are awakened:

java

深色版本



```
if (/*method to check if it's a pickup-only elevator*/) {
  	this.stateMap.addOnly(nowFloor);
}
this.stateMap.addService(nowFloor);
open();
```

Additionally, `passenger.getToFloor()` was replaced with `passenger.getDemand()`. Since passenger demands are now broken into multiple sub-requests, we handle them sequentially after obtaining the path, with `getDemand()` fetching the head element of the queue:

java

深色版本



```
public int getDemand() {
    return this.demand.getFirst();
}
```

#### Thread Interaction and Overall Workflow

The overall structure remains largely unchanged from the second assignment, with differences including:

- Additional conditional checks when opening/closing doors, addressed by the new `stateMap` class.
- For transfers, the `demand` queue is updated whenever passengers enter the `waitQueue`, taking the head element as the current main request, and determining whether to re-enter the `waitQueue` based on whether the destination has been reached.

#### Synchronization Blocks and Locks

Similar to the first assignment, with synchronized keywords added to methods in the new `stateMap` class for adding/removing elements corresponding to floor entries.

#### UML Class Diagram



![UML Class Diagram](https://pic.superbed.cc/item/66ab4640fcada11d3763d622.png)



#### Bug Analysis

No bugs were detected during mid-term testing, but self-testing using the evaluation machine revealed an issue: even with the `addService` method, the number of serving elevators on the same floor could still exceed the limit. The fix involved changing the `if` statement to a `while` loop to ensure proper handling.

##### Debugging Methods

Testing with random data using the evaluation machine helps identify problematic areas. If errors occur, error messages guide pinpointing the faulty section and identifying problematic methods.

An unstable bug appeared during strong testing, running fine locally but resulting in real_time_limit_exceed on the official test environment, throwing a `NullPointerException`:

javascript

深色版本



```
Exception in thread "Thread-6" java.lang.NullPointerException
    at StateMap.findPath(StateMap.java:100)
    at Distribute.run(Distribute.java:32)
```

After checking the method, it was uncertain whether the issue stemmed from a lack of locking. Adding locks resolved the issue, though only one thread accessed the method, suggesting the problem might not have been thread safety related.

## Summary of Three Assignments

#### UML Collaboration Diagram



![UML Collaboration Diagram](https://pic.superbed.cc/item/66ab465dfcada11d3763d758.png)



#### Analysis Content

Stable content includes basic elevator operations such as transporting passengers, opening/closing doors, moving up/down, etc. The distributor thread sends passengers from the `waitQueue` to elevator waiting queues, and elevator threads pick up passengers from these queues, carrying them along with other passengers en route.

Variable content includes maintenance requests, custom requirements like capacity and operation time, affecting time and output operations within elevator threads. Accessibility requirements mean the distributor considers whether both the current and demand floors are reachable when allocating passengers, and elevators must consider these factors when opening/closing doors and picking up passengers. Door opening limits introduce additional conditions beyond the original door operations.

## Reflections

#### Thread Safety

Thread safety is crucial in multi-threaded programming. To control access to shared resources, synchronization through locking mechanisms is essential. In this assignment, I used only the `synchronized` keyword, applying it to methods and calling `notifyAll()` at the end to wake up other threads waiting for access.

#### Hierarchical Design

This unit deepened my understanding of hierarchical design. Using the producer-consumer pattern makes it easy to understand the role of each class. Producers allocate items (passengers) to conveyor belts (`waitQueue`), while consumers only need to take out items. This approach allows each class to focus on its own tasks, adhering to the principle of "high cohesion, low coupling," making the system clearer and easier to understand (and debug).

#### Personal Insights

This unit has been incredibly rewarding, transitioning from knowing nothing about multi-threading to developing a sophisticated multi-threaded elevator scheduling program. Key takeaways include:

- Identifying problems is far more important than solving them. The lesson from the second assignment taught me that modifying potential bugs without truly understanding the problem wastes significant time and effort.
- The importance of collaboration. Before this unit, assignments were typically tackled alone. However, for the second assignment, consulting numerous peers and upperclassmen ultimately led to resolving the issue. Without their help, completing the assignment would have been impossible.

Thank you to all the classmates who provided enthusiastic assistance ^_^; I look forward to gaining even more knowledge on this journey.

---



## 介绍

第二单元作业的基本训练目标是模拟**多线程实时电梯系统**，三次作业为迭代开发。

- 第一次作业要求设计一个具有功能为上下行，开关门，以及模拟乘客进出的电梯系统，目标是让我们熟悉线程的创建、运行等基本操作，熟悉多线程程序的设计方法；
- 第二次作业新增加了模拟电梯系统扩建和日常维护时乘客的调度的功能，目标是让我们掌握线程安全知识并解决线程安全问题，同时在架构上围绕线程之间的协同设计层次架构；
- 第三次作业新增电梯系统调度参数，具体来说是对每一层楼正处于开门状态的电梯数量加以限制，同时电梯不一定满足每层楼可达， 目标是让我们掌握线程之间的交互，强化线程之间的协同设计层次架构。

点击此处可以参考我的代码：https://github.com/LeostarR/Object-oriented-Design-and-Construction

## 第一次作业总结

### 设计与架构

本次作业需要完成这些工作：

1. 设计一个**队列**类，用于进行乘客进入/移除，同时还需有判断是否为空/是否结束和设定结束等方法
2. 设计一个**调度器**类，作为总等候队列与电梯之间的桥梁，需要完成对乘客的分配工作
3. 设计一个**电梯**类，能够完成题目要求的任务
4. 在`Main`中启动调度器线程和电梯线程，并将输入（如果有）送给总等候队列，结束时将等候队列设定结束

> 说明：本次作业课程组已经为我们提供了输入输出的接口，因此不再考虑解析输入的请求和格式化时间戳输出。

#### 队列类

##### `PassengerQueue`:

![](https://pic.superbed.cc/item/66ab4496fcada11d3763b4c2.png)

 `addPassenger(Passenger)`和`removePassenger(Passenger)`用于添加和移除乘客，`getOnePassenger()`用于分配乘客时按时间先后取出，`setEnd(boolean)`用于设定队列结束。其他方法用于返回该队列的一些属性，例如是否结束，是否为空以及队列所有乘客组成的集合(`ArrayList<Passenger>`)。

#### **调度器类**

##### `Controller`:

![](https://pic.superbed.cc/item/66ab452ffcada11d3763c8b6.png)

重写了`run()`方法将乘客分配。

分配策略：采取均匀分配的策略。按照乘客到来的时间先后顺序将总等候队列中的乘客均匀分配给电梯的等候队列。（~~所以性能不好$_$~~)

一个示例：

```java
while (true) {
        if (/*结束条件*/) {
            //将所有电梯等候队列设为结束//
            return;
        }
        Passenger passenger = waitQueue.getOnePassenger()//按时间顺序取出//;
        if (passenger == null) {
            continue;
        }
        elevatorQueues.get(/*index*/).addPassenger(passenger);//分配
}
```

#### 电梯类：

##### `Elevator`:

![](https://pic.superbed.cc/item/66ab4549fcada11d3763c9b2.png)

 重写了`run()`方法使电梯运行，总的模式是这样的：根据等候队列中的顺序，确定主请求然后将主请求的乘客送到目的地。在这个过程中分为接主请求乘客和送主请求乘客两部分，这两个过程中在未超载（主请求计算在内）且运行方向相同的情况下可进行捎带。在每一个主请求到达的时候将电梯内部先到的乘客确定为主请求，继续运行直至电梯内部为空，再从等候队列中取出乘客。

```java
@Override
public void run() {
    while (true) {
        if (/*结束条件*/) {
		    return;
        }
        Passenger passenger = queue.getOnePassenger();//queue(ArrayList<Passenger>)是该电梯的等候队列
        if (passenger == null) {
            continue;
        }
        //分两步：
        //1.将最早的乘客起始点设为主请求，接人
        this.setTarget(passenger.getFromFloor());
        forward(passenger);
        //2.将该乘客目的地设为主请求，送人
        this.setTarget(passenger.getToFloor());
        forward(passenger);
        while (!this.list.isEmpty()) {
            //将剩下的乘客逐一运送
        }
    }
}
```

`forward(Passenger p)`方法用于**接**或**送**乘客`p`，同时还需满足捎带：

```java
private void forward(Passenger p) {
    while (this.nowFloor != this.targetFloor) { //捎带：方向一致，目标小于主目标
        if (/*捎带或中途下人条件是否成立*/openOrNot()) {
            //开门、出人、进人、关门
        }
        this.move();//移动一层
    }
    //到达指定层
    this.open();
    //1.如果p在电梯内且到了目的地，先让p下电梯
    //2.如果捎带的乘客目的地也在此层，下电梯
    //3.如果p还没上电梯，先让p进电梯
    //4.根据剩余容量捎带此层满足条件的乘客
    this.close();
}
```

`in()`,`out()`,`open()`,`close()`,`arrive()`均需要输出指定内容，`addPassenger()`和`removePassenger()`是电梯内队列的增减乘客操作方法（这里的方法和前文的队列类不同，就是很简单的删减操作，因为每一个电梯内队列只会被当前电梯操作，无需考虑线程安全的问题），都很容易实现~

#### 线程之间的交互和总运行流程

开始输入，官方接口接受输入，`Main`将每一个`Passenger`送到总等候队列`waitQueue`（相当于生产者消费者模型中的托盘），调度器是电梯和乘客之间的桥梁，按照某种方式将`waitQueue`中的乘客放入不同电梯的等候队列(注意不是电梯内部)。而此时电梯和等候队列的关系也正好对应了消费者和托盘的关系，不同的是，一个等候队列仅对应一个电梯，因此每个电梯只需考虑将自己的等候队列里乘客处理完毕，直至收到结束信号并且等候队列和电梯内均为空则结束线程。

#### 同步块与锁

和普通的程序不同，多线程编程中要注意线程安全的问题。例如，如果两个线程同时对一个对象进行写的操作，很有可能出现冲突和异常。因此要保证该对象每次只被一个线程访问，可以在所有可能被多个对象同时访问的成员方法加上synchronized关键字，在方法末尾调用`notifyAll()`唤醒其他线程。锁和同步块中处理语句之间的关系是，当一个线程进入同步块时，它会自动获取同步块所关联的锁对象，当它退出同步块时，它会自动释放锁对象，这样可以保证同步块中的处理语句不会被其他线程干扰。例如：

```java
public synchronized void removePassenger(Passenger p) {
    //Your Method
    notifyAll();
}
```

#### UML类图

![](https://pic.superbed.cc/item/66ab4564fcada11d3763cb0b.png)

#### bug分析

本次作业的中测在~~写调度器之前~~频繁超时，加上调度器之后就没有bug了。

强测和互测各出现了一个不能稳定复现的超时bug，大概率是高频发时会引发某些问题，导致乘客没进或没出以至于程序无法终止。检查代码的时候我发现在捎带乘客那一部分实际设定的电梯容量是5（@_@)，改正之后居然就能够解决了。


## 第二次作业总结

### 设计与架构

在第一次作业基础上需要增加这些工作：

1. 将**电梯**类的属性变量添加到构造方法中以满足个性化添加电梯，并在具体实现中将常量替换为该电梯的属性
2. 在**队列**类中新增和`maintain`有关的方法和变量以满足维修的需求
3. 在**电梯**类的运行方法中新增判断是否`maintain`的方法以及处理措施的方法
4. 修改线程结束的条件(`waitQueue`的输入来源不止一个main了，还有维修的电梯)
   

#### 队列类

##### `PassengerQueue`:

![](https://pic.superbed.cc/item/66ab457ffcada11d3763cc5f.png)

增加了`setMaintainSymbol(int)`， `isMaintained()`和`addMaintainList(ArrayList<PersonRequest)`三个方法。其中`setMaintainSymbol(int)`和`isMaintained()`分别用于设定维修标志和判断维修标志,`addMaintainList()`将参数`ArrayList<PersonRequest> list`全部加入到此对象的`List`中，可以适应将与维修电梯相关人员加入到`waitQueue`的操作。

#### 调度器类

##### `Distribute`:

~~对的，不是打错了，是改名了~~

由于电梯也能将`waitQueue`加入乘客，因此需要修改调度器的结束条件：

```java
while (true) {
        if (waitQueue.isEnd() && waitQueue.isEmpty()) {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (!waitQueue.isEmpty()) {
                continue;
            }
            for (PassengerQueue elevatorQueue : elevatorQueues) {
                elevatorQueue.setEnd(true);
            }
            return;
        }
        //Your Method
}
```

对比一下之前的：

```java
while (true) {
        if (waitQueue.isEnd() && waitQueue.isEmpty()) {
            for (PassengerQueue elevatorQueue : elevatorQueues) {
                elevatorQueue.setEnd(true);
            }
            return;
        }
        //Your Method
}
```

其实仅仅时增加了中间一部分，因为考虑到maintain这个过程是需要时间的，如果仅仅在`waitQueue`为空时结束，那么一些收到maintain信号的电梯可能没来得及放出乘客，此时关闭调度器可能会使这些乘客无法参与分配从而永远到达不了目的地~~_~~

所以我在满足原结束条件的情况下使调度器休眠固定时间，这也是由于课程组要求两层`arrive`以内必须完成`maintain`的操作，因此这个时间不会太难控制。如果休眠之后仍满足该条件，则结束。

第二次作业的调度策略与第一次作业的差别不大，此处不再赘述。


#### 电梯类

##### `Elevator`:

在第一次作业上新增了一个方法：

![](https://pic.superbed.cc/item/66ab459ffcada11d3763ce23.png)

用于处理维修请求，一个示例（删去了很多细节代码，仅供参考）：

```java
private void dealMaintain(PersonRequest p) {
    ArrayList<PersonRequest> arr = new ArrayList<>(list);
    if (/*p不在电梯中*/) {
       waitQueue.add(p);
    }
    for (PersonRequest person : arr) {
        if (/*乘客未到达*/) {
            //修改person的属性fromFloor为当前楼层
            person.setFromFloor(nowFloor);
            waitQueue.add(person);
        }
        this.out(person);
    }
    waitQueue.addAll(this.queue.getList());//别忘了等候队列的可怜人:)
    this.maintainFlag = 1;
}
```

同时在其他方法比如`forward()`和`run()`中增加新内容以适应维修的需求：

```java
	while (this.nowFloor != this.targetFloor) { 
            //需要在正常上下客之前判断maintain
           if (this.queue.isMaintained()) {
                this.dealMaintain(p);
                return;
            }
            //Originated Method
        }   

	@Override
    public void run() {
        while (true) {
            ......
            if (/*维护条件*/) {
                /*输出维护信号*/
                return;
            }
            ......
            forward(passenger);
               if (this.maintainFlag == 1) {
                continue;
            }
            while (/*电梯非空*/) {
                ......
                forward(passenger);
                if (this.maintainFlag == 1) {
                    break;
                }
            }
        }
```

总体还是通过改变`maintainFlag`来实现的。


#### 线程之间的交互和总运行流程

总体结构变化不大，与第一次作业的区别：

- 调度器并不会在标准输入结束之后结束，而是还要确定所有维护电梯均被维护才能结束
- 电梯线程可能会因为维护而提前结束，通过设定对应的等候队列的`maintainSymbol`，然后将其从`elevatorQueues`中`remove`可以切断调度器与之的联系，不会再给此电梯分配乘客，接着每个电梯根据是否收到此`maintainSymbol`来进行相应操作

 

#### 同步块与锁

和第一次作业区别不大。


#### UML类图

第二次作业使用了官方的`PersonRequest`类，因此比第一次作业少一个类，区别不大。

![](https://pic.superbed.cc/item/66ab45b7fcada11d3763cff6.png)

#### bug分析

本次作业应该是这个单元最折磨的一次了，因为我的代码出现了大面积CPU超时的问题，中测甚至提交了9次才通过:(

那一次周六对着电脑看了一天都没看出来问题，看到晚上晚上几近崩溃。每一次提交得到的10个CPU超时真的很揪心......

![](https://pic.superbed.cc/item/66ab45d2fcada11d3763d135.png)

求助了助教还有很多同学，最终发现了问题的所在：

我一开始并没有让调度器在所有维修电梯完成且分配完成之后休眠，而是一直等到了所有乘客离开整个系统。

> 程序的输入输出为**实时交互**，评测机可以做到在某个时间点投放一定量的输入

也就是说输入结束的时间应该是最后一条指令实际被接受到的时刻附近。而正是这样，我觉得`setEnd`（我的理解偏差，这个时候我认为输入结束是指将数据复制到终端然后按下回车的时刻）之后调度器还必须等待最后一条指令，因为最后一条指令也可能会将`waitqueue`中添加乘客。所以就有了下面的大量无意义循环：

```java
Distribute.java:

        while (true) {
            ......
            if (waitQueue.isEnd()) {
                for (PassengerQueue elevatorQueue : elevatorQueues) {
                    elevatorQueue.setEnd(true);
                }
            }
            if (waitQueue.isEmpty() && waitQueue.isEnd() && table.isEmpty()) {
                return;
            }
            Passenger passenger = waitQueue.getOnePassenger();//每次出来的结果都是null
            if (passenger == null) {                          
                continue;
            }
            .....
        }



PassengerQueue.java:

        public synchronized Passenger getOnePassenger() { //按时间先后取出(先来后到)
            if (queue.isEmpty() && !this.isEnd()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (queue.isEmpty()) {
                return null;
            }
            ......
    	}
```

已经将`waitQueue`设定`setEnd()`，每次在`getOnePassenger`中的第一个if会直接跳过，而此时`queue`又确实为空会返回`null`。但整个系统还有乘客（`!table.isEmpty`），这样会进入大量循环返回`null`然后不断`continue`，占用大量的`CPU`资源导致超时。解决方法很容易，只要能理解到输入结束的时间就很好改了，正确的结束方式已经在前文的第二次作业总结->调度器类中解释了。

强测中出现了很多捎带的问题，发现漏掉了捎带的条件。

##### debug方法

使用了很多检查CPU时间的工具，都没能起到实质性的作用，不如直接在循环里`print(something)`，如果某个方法打印输出的东西达到了几十万行，就很好定位了：

```java
public void run() {
    while (true) {
        //System.out.println("**********distributeI");
        if (waitQueue.isEnd() && waitQueue.isEmpty()) {
            ......
        }
        ......
        //System.out.println("**********distributeO");
    }
}
```

不断将两条语句向中间靠近，找到循环最多的一个分支，就很容易发现问题了。


## 第三次作业总结

### 设计与架构

在第二次作业基础上，需要新增这些内容：

1. 对每层楼可开门的数量加以限制
2. 考虑到部分电梯的可达性，需要在分配乘客之前确定路径

为了解决上述问题，我新增了一个`StateMap`类并提供诸多方法用于解决新的问题。

 

#### 个性化需求类

##### `StateMap`:

![](https://pic.superbed.cc/item/66ab45effcada11d3763d272.png)

对于限制开门数量的要求，可以这样实现：

![](https://pic.superbed.cc/item/66ab4606fcada11d3763d395.png)

`serviceMap`的`value`对应了每层楼服务中的电梯数量，`key`指示楼层，因此初始时每层楼服务数量都是0。

开门时，直接调用`addService()`，由于`synchronized`的存在，每次只会有一个电梯访问该方法，若数量大于规定数量，则等待，直至该楼层服务中电梯数量小于规定数量，被唤醒，然后该楼层服务中电梯数量加1：

```java
public synchronized void addService(int floor) {
    while (this.serviceMap.get(floor) >= 4) {
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    serviceMap.put(floor, serviceMap.get(floor) + 1);
    notifyAll();
}
```

关门则直接调用`removeService()`:

```java
public synchronized void removeService(int floor) {
    serviceMap.put(floor, serviceMap.get(floor) - 1);
    notifyAll();
 }
```

对于只接人的电梯数量则跟上述方法类似，只不过需要新增方法来判断该电梯是否为只接人的。

为了解决路径问题，我定义了两个HashMap：

![](https://pic.superbed.cc/item/66ab4620fcada11d3763d4b8.png)

`accessMap`指示每个电梯可以直达的楼层，`pathMap`指示每层楼可以直达的楼层（类似于邻接矩阵）。

通过`findPath(Passenger p)`方法返回一个`LinkedList<Integer>`类型的需求队列，队列头部是该乘客下一个目的地，队列尾部是乘客的最终目的地。

#### 调度器类

##### `Distribute`：

在调度的时候需要考虑乘客的当前需求楼层和所在楼层对于该电梯是可达的，在此基础上均匀分配：

```java
LinkedList<Integer> demand = stateMap.findPath(passenger);
passenger.updateDemand(demand);
do {
    i = (i + 1) % elevatorQueues.size();
} while (!(/*乘客的当前需求楼层和所在楼层对于该电梯是可达的*/));
elevatorQueues.get(i).addPassenger(passenger);
```

#### 电梯类

##### `Elevetor`:

需要在开门前，关门后对`stateMap`做处理，如果数量不满足条件则会`wait()`直至被唤醒：

```java
if (/*判断只接人的方法*/) {
  	this.stateMap.addOnly(nowFloor);
}
this.stateMap.addService(nowFloor);
open();
```

还需要把原来`passenger.getToFloor()`替换为`passenger.getDemand()`。因为现在乘客的总需求被拆分为多个子请求，我们在得到路径之后需要逐一处理子请求，`getDemand()`就是取出队列头部元素：

```java
public int getDemand() {
    return this.demand.getFirst();
}
```

#### 线程之间的交互和总运行流程

总的结构区别不大，与第二次作业的区别：

- 开关门时需要有额外的判断条件，新增`stateMap`类解决
- 对于换乘，我们在每次乘客进入`waitQueue`时更新`demand`队列，每次取出头部元素作为当前主请求，每次下电梯时判断是否为终点，再选择是否重新进入`waitQueue`

#### 同步块与锁

和第一次作业区别不大。仅在新类`stateMap`中对应的添加删除对应楼层元素的方法中使用了synchronized关键字。

#### UML类图

![](https://pic.superbed.cc/item/66ab4640fcada11d3763d622.png)

#### bug分析

本次中测未测出bug，但是在使用评测机自行测试的时候发现了这样一个问题：即使设定了`addService`这样的方法但是还是会出现同一层服务电梯数量超过的情况，后来想清楚了：

```java
//之前的方法:

public synchronized void addService(int floor) 
    if (this.serviceMap.get(floor) >= 4) {
       //Your Method(wait......)
    }
    //Your Method
	notifyAll();
}
```

在这里要注意到`serviceMap`是一个`HashMap`类型，所有楼层的信息全部存储在这里。假如5楼开门的电梯已经达到最大数量，其他准备在5楼开门的电梯就会等待。但是要注意其他楼层的开关门操作也会改变该HashMap，但不一定改变此层楼对应的键值，这样5层不该开门的电梯有可能被错误地唤醒。修改方法很容易，把`if`改成`while`就行了，加一次判断即可。

##### debug方法

先用评测机评测随机数据，如果出现错误，先根据错误信息定位错误区间，确定出问题的方法。

强测出现了一个不能稳定复现的bug，在本地跑了好多遍都没有什么问题，官方给出的解释信息是real_time_limit_exceed，出现的异常信息：

```javascript
Exception in thread "Thread-6" java.lang.NullPointerException
    at StateMap.findPath(StateMap.java:100)
    at Distribute.run(Distribute.java:32)
```

检查了这个方法，不能够确定是否是没有加锁导致的。加锁之后，交上去，过了。但是在代码中只有一个线程能够访问此方法，所以很疑惑，感觉也不一定是线程安全的问题。


## 三次作业总结

#### UML协作图

![](https://pic.superbed.cc/item/66ab465dfcada11d3763d758.png)

#### 分析内容

稳定的内容：其实也就是第一次作业的内容，运送乘客，电梯开关门上下行。调度器线程从`waitQueue`中把乘客送给电梯的等候队列，电梯线程从等候队列中接人，捎带，运送乘客。

易变的内容：加入了维修需求之后，`waitQueue`接受的输入不止有标准输入了，还有电梯线程。自定义需求（容量，操作时间）使得电梯线程中的时间操作，输出操作需要做出相应更改。可达性需求出现后，调度器分配时要考虑乘客当前和需求楼层均可达，电梯开关门和捎带也必须考虑这两点。限制开门数量的需求出现后，在原来开关门的基础上需要额外增加条件。


## 心得体会

#### 线程安全

线程安全问题在多线程编程中非常重要。由于存在多个线程同时共享一个资源的情况，我们需要严格控制能够访问这一资源的数量。方法就是加锁。本次作业中我只使用了`synchronized`这一种锁~~（其实也只会这一种）~~，在每个方法之前加上synchronized关键字，在方法最末尾`notifyAll`唤醒其他可能正在等待访问此资源的线程。

#### 层次化设计

本单元的作业进一步加深了我对层次化设计的理解。使用生产者消费者模式可以很容易地理解每一个类扮演的角色是什么。如果是生产者，那么这个类里完成所有分配商品（乘客）到传送带（`waitQueue`）的工作，如果是消费者，只需要完成取出商品的工作即可。这样每个类能够仅考虑自己的工作，一定程度上吻合了“高内聚，低耦合”的思想，同时层次更加清晰，易于理解~~（更容易找bug）~~。

#### 一些感想

本单元真的是收获颇丰的一单元，从对多线程的一无所知到能够编写出这样多功能的多线程协作电梯调度程序，已经是一件非常有成就感的事。在这个过程中，我主要有这样几点认识：

- 寻找问题比解决问题重要得多。这是本单元第二次作业给我的教训。在没有真正发现问题之前修改一些自认为可能的bug，非常浪费时间浪费精力。
{% blockquote Charles Dickens, David Copperfield %}
The difficulty is not in finding a subject to talk about, but in discovering a subject that will reveal something in your own nature.
{% endblockquote %}
- 协作与合作的重要性。在这之前，每次的作业基本都是自己慢慢琢磨出来的。但是这单元的第二次作业我请教了大量的同学，问了很多的高年级同学，最终才发现了这个问题。没有这么多人的帮助我不可能完成这一次作业。

感谢这么多同学的热心帮助^_^，希望在这条路上能够取得更大的收获。
