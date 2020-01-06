# TODO
- Send request to connect:
    - how to look up the nodeInterface, the stub?
- A separated class for messages
    - Class Node implement messages maybe?
- Currently the algorithm works for the first step ONLY
    - Generalize
- Introduce dalays in threads
    - for now ONLY delay at starting the threads
    - later, introduce delays in messages
- think about queues as well
# Report
- thread.start is now happening after creating the nodes and the links.
- states has added to both link and node classes *(check if there is a better option than enum)*
- MOE for each node is calculated *this is the 1st step*            