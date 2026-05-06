Can contain shader nodes and nodes from other node graphs.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/dba9b7328da44b81aeba92da52f7acd2~tplv-goo7wpa0wc-image.image)
**Node Graph** nodes are used to encapsulate node graphs that can be reused across different materials. You can regard them as containers that can include shader nodes and other nodes. To frequently use certain repeated node combinations (subgraphs), define them as reusable **Node Graph** modules.
The graph within a **Node Graph** node is almost identical to the node graph in Shader Graph. The main difference is that you can define any number of custom Inputs and outputs in the node graph. You need to specify a name and type for each custom Input and output.
## Node usage instructions
### Add Node Graph nodes directly
You can add Node Graph nodes directly, then double-click the node to edit the node logic within the node graph.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/6c489132a6294c1da8174a9efae998f9~tplv-goo7wpa0wc-image.image)
You can add Input nodes and output nodes to the node graph as needed.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/3535f42acaec4a949cc8ace11a949cd6~tplv-goo7wpa0wc-image.image)
### Combine multiple nodes into a Node Graph node
The following demonstrates how to combine multiple nodes into a Node Graph node.
Select the nodes to combine with the mouse, right-click, and select **Compose Node Graph**.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/74360b88efa64bd3aa6f4e9d9f991526~tplv-goo7wpa0wc-image.image)
Then, you will see the combined Node Graph.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2129953415db477080f3f10892391019~tplv-goo7wpa0wc-image.image)
You can double-click the Node Graph to view and edit its node logic.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/63ea1f7c8d814ce7b34cba408ac94cb1~tplv-goo7wpa0wc-image.image)
### Create Node Graph instances based on Node Graph nodes
You can right-click Node Graph and select **Create Node Graph Instance** to create a Node Graph instance.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/2fe41f0597214891bf48f4b46ce56b54~tplv-goo7wpa0wc-image.image)
The Node Graph instance inherits the node logic of the original Node Graph. If you modify the original Node Graph, the related Node Graph instances will be updated synchronously. Conversely, if you modify a Node Graph instance, the original Node Graph will also be updated synchronously.
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/235eeb782182480bbffd266e5f817818~tplv-goo7wpa0wc-image.image)
### 













