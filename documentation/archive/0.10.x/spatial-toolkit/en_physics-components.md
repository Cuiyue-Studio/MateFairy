This article introduces the physics components in Spatial Editor.
Physics components can be added to or removed from entities. In .usda files, physics components are of type `SpatialComponent`. This is a component type defined by Spatial Editor and is not native to USD.
## RigidBody
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b2c6b64bcbb24fbcb2da7c599100452b~tplv-goo7wpa0wc-image.image)
This component assigns physical properties to entities, enabling them to respond to gravity, collisions, and forces.
| **Parameters** | **Description** |
| --- | --- |
| Mode | Sets the physics mode of the rigid body: <br>  <br> * **Dynamic**: The rigid body is fully controlled by the physics engine and responds to gravity, collisions, and forces. <br> * **Kinematic**: The rigid body is not affected by forces or gravity; its movement is directly controlled by animation or code. |
| AffectedByGravity | Controls whether the entity is affected by gravity. Default: Enabled. |
| Mass | The mass of the entity (kg). Default: 1. |
| Center Of Mass | Defines the position of the entity's center of mass in the local coordinate system. Default: (0, 0, 1). |
| Inertia | The inertia tensor is a second-order physical tensor that describes an object's rotational inertia properties, representing its resistance to changes in rotational motion. Default: (1, 1, 1). |
| Linear Damping | Resistance opposite to the direction of motion; the larger the value, the faster the velocity decays. Default: 0, indicating no resistance. |
| Angular Damping | Resistance opposite to the direction of rotation; the larger the value, the faster the angular velocity decays. Default: 0.05. |
| Collision Detection | Sets the collision detection method; different modes vary in accuracy and performance cost: <br>  <br> * **Discrete**: Collision is detected only at the end of each physics time step. This mode has the lowest performance cost but may cause fast-moving objects to pass through other thin objects (known as the "tunneling effect"). <br> * **Continuous**: Collision is calculated by predicting the object's trajectory within the time step, which can prevent the tunneling effect. This mode increases computational cost and is suitable for high-speed objects such as bullets. <br> * **Continuous Dynamic**: Continuous detection is performed between two dynamic rigid bodies (non-static or non-kinematic rigid bodies) only when both have continuous detection enabled. This is a performance optimization. <br> * **Continuous Speculative**: A continuous detection method with lower computational cost. It prevents penetration through speculative contacts, achieving a balance between performance and accuracy. |
| Translation Locked | Restrict the movement of the rigid body along one or more axes of x, y, or z. |
| Rotation Locked | Restrict the rotation of the rigid body along one or more axes of X, Y, or Z. |
## Collision
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/8827807add1a49b48058eb453f757145~tplv-goo7wpa0wc-image.image)
Adds a collider to the object.
| **Module** | Parameter | **Description** |
| --- | --- | --- |
| Collision | Mode | Set the behavior mode of the collider. <br>  <br> * **Collider_Full**: (Default) Standard physical collider. Can physically collide with other colliders, such as bouncing or blocking. <br> * **Trigger_Full**: Fully functional trigger. It does not produce physical collision effects, but can detect whether other objects enter, stay within, or leave its range. Commonly used to trigger scripts or events. <br> * **Trigger_Lite**: Lightweight trigger. Functions similarly to **Trigger_Full**, but with lower computational overhead and higher performance. |
|  | Shape | Set the geometric shape of the collider. Options include: <br>  <br> * **Box**: (Default) Box-shaped collider. <br> * **Sphere**: Sphere-shaped collider. <br> * **Capsule**: Capsule-shaped collider. |
|  | Translation | Local offset of the collider relative to the object's origin. You can also click the **Recalculate** button. The Spatial Editor will automatically calculate based on the model mesh. <br> This parameter appears only when the **Shape** parameter is set to **Capsule** or **Box**. |
|  | Rotation | Rotation angle of the collider in the local coordinate system. You can also click the **Recalculate** button. The Spatial Editor will automatically calculate based on the model mesh. <br> This parameter appears only when the **Shape** parameter is set to **Box**. |
|  | Size | Size of the collider in the local coordinate system. You can also click the **Recalculate** button. The Spatial Editor will automatically calculate based on the model mesh. <br> This parameter appears only when the **Shape** parameter is set to **Capsule** or **Box**. |
|  | Height | Height of the collider in the local coordinate system. You can also click the **Recalculate** button. The Spatial Editor will automatically calculate based on the model mesh. <br> This parameter appears only when the **Shape** parameter is set to **Capsule** or **Box**. |
|  | Radius | Radius of the collider in the local coordinate system. |
| Collision Filter <br>  | Group | Specify the collision group to which the entity belongs: <br>  <br> * **Default**: (Default) Default collision group. <br> * **All**: The entity belongs to all collision groups. |
|  | Mask | Defines the set of target collision groups that an entity is allowed to collide with: <br>  <br> * **Default**: Indicates that the entity only collides with entities belonging to the default collision group. <br> * **All**: (Default) Indicates that the entity can collide with entities from all collision groups in the scene. |
| Physic Material | Static friction | The frictional force on the surface of a rigid body when it is stationary; the default value is 0.8. |
|  | Dynamic friction | The frictional force on the surface of a rigid body when it is in motion; the default value is 0.8. |
|  | Restitution | The coefficient of restitution of the rigid body's surface, representing the proportion of kinetic energy retained after a collision. The value range is 0 to 1. The greater the value, the stronger the elasticity after collision, the higher the proportion of retained velocity, and the more pronounced the rebound effect. The default value is 0.8. |
## Physics Velocity
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/33458551fd704eb2b1e6598f0a333ca6~tplv-goo7wpa0wc-image.image)
Sets the linear and angular velocity of the rigid body.
| **Parameter** | **Description** |
| --- | --- |
| Linear Velocity | Defines the linear velocity of the rigid body along the X, Y, and Z axes of the world coordinate system. |
| Angular Velocity | Defines the angular velocity of the rigid body along the X, Y, and Z axes of the world coordinate system. |
## Physics Force
![Image](https://p9-arcosite.byteimg.com/tos-cn-i-goo7wpa0wc/b0c28e76abfc45f6a4f0ed5a802b1c80~tplv-goo7wpa0wc-image.image)
Applies force and torque to the rigid body to control its movement in the physics simulation.
| **Parameter** | **Description** |
| --- | --- |
| World Force | Defines a force applied to the rigid body in the world coordinate system. The unit is newtons (N). |
| Local Force | Defines a force applied in the rigid body's own coordinate system (local coordinate system). The unit is newtons (N). |
| World Torque | Defines a torque applied to the rigid body in the world coordinate system. The unit is newton-meters (N·m). |
| Local Torque | Defines a torque applied in the rigid body's own coordinate system (local coordinate system). The unit is newton-meters (N·m). |

