# ZRP-adaptation-for-hybrid-routing 
deployment of an ad hoc wireless network based on lightweight equipment wifi and bluetooth: adaptation of a routing and autoconfiguration protocol
--------------------------------------------------------------------------------
##                ZRP-adaptation-for-hybrid-routing
--------------------------------------------------------------------------------
## 1. Introduction

    This project is an adaptation for ZRP routing protocol for Ad Hoc wireless network based on lightweight equipment wifi and bluetooth.
    ZRP is a routing and autoconfiguration protocol for Ad hoc wifi network and for equipment with average energy. Here we adapt this protocol
    for lightweight equipment, poor in energy and memory. we also adapt this protocol to use bluetooth also for routing, so the protocol become
    hybrid.
    The architecture of the routing system having already been chosen, it is then appropriate to implement the various modules of 
    our architecture. For the implementation, we focus on the most important functions. The implementation of our system follows 
    several steps ranging from the radiocommunication interfaces that will be used, the development tools and the test environment. 
    The most important thing for us at the end of the tests is to know that the system, once functional, effectively allows 
    the deployment of applications for communication in an ad hoc network.
    
NB. This project was build on 2011

## 2. Test - Choice of wireless communication interface

   The radiocommunication interfaces that we use here are Bluetooth and Wifi.
    But we will focus more on Bluetooth, for several reasons. Bluetooth is the radiocommunication technology that is most 
    found on mobile phones and Smartphones and it also consumes less energy [HLA 06]. The lightweight equipment that we have 
    at our disposal is mostly equipped with Bluetooth. As a result, Bluetooth is more suitable for our tests compared to Wifi.

   As a development kit, we used the JDK 1.6, for the Java programming language. We work more specifically with J2ME which 
   is the technology available for mobile development in Java with the JDK1.6. We chose the Java language because it is 
   multiplatform and it allows us to maintain our system independently of the platform on which it will run.

### Development Environment
Netbeans is an integrated development environment (IDE) developed by Sun
microsystem. Its use is justified by its ease of installation, use and
configuration.     

### Implementation of the routing system
We carried out our development in two phases. The first phase consisted
in the actual implementation of our system. On the other hand, the second phase consists
just in the development of a chat application. The chat application was developed
to allow us to perform tests on our system. The number of hops in our
system by default is two hops, but it is configurable.
Routing will be done proactively within a radius of two hops, but beyond
it will be reactive.
Within the zones consisting of Bluetooth devices, routing will be
done only in the master equipment of each piconet. In the figure below
we can see the addition of a new node in the routing table according to the number
of hops that has been set. The routing module
will therefore periodically query the physical layer to find out if there is a new
node in the network. This allows the routing table to be updated.

### Test

As we mentioned in the previous part, the chat application
is used to perform the tests. Because if an application like the chat, once deployed on
our system works, it would mean that we have achieved our goal. The tests were first performed on the Netbeans emulator,
 the latter simulates the Bluetooth and wi protocol well. The tests on the emulator were conclusive,
since in this virtual environment, the packets sent always arrive at their destination,because there is no influence
 on radio communication.The last tests were performed in a real environment using two Nokia C3 phones and a Motorola L7 phone. 
 These tests were less conclusive compared to the tests done on the emulator. We were able to take screenshots of the system on 
 the two Nokia C3 phones. Figure  shows the home page of the chat application, which has a set of menus. To send a message, 
 you must choose the send message menu. To  have a view of the network, you can choose the menu rescan equipment .

