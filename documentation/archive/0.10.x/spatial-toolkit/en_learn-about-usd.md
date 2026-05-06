This article introduces the 3D model formats supported by Spatial Editor.
Spatial Editor supports only USD (Universal Scene Description) as the import format for 3D models. USD contains two core concepts: Prim (the smallest logical container unit) and Reference (the reuse mechanism for Prim), which together form the foundation for USD's efficient management of 3D scenes.
## What is USD
[USD](https://openusd.org/release/index.html) is an open-source 3D scene description format developed by Pixar. USD builds a hierarchical structure with Prim and enables cross-file resource reuse through Reference, allowing flexible organization, efficient editing of complex scenes, and supporting team collaboration. USD includes the following four file formats, all designed around the Prim and Reference mechanisms:

* .usd: Can contain data in a human-readable text format (ASCII) or binary format.
* .usda: A human-readable text format (ASCII), suitable for editing and version control. In Spatial Editor, scenes (Scene) are stored in the .usda file format.
* .usdz: A single-file packaging format that integrates models, maps, and other resources, suitable for AR scene distribution. Internally, data is still stored in a Prim hierarchy.
* .usdc: A binary format that is compact in size and loads efficiently, does not contain external resource references, stores Prim data in binary encoding, and is suitable for high-performance production environments.

## The basic container of USD: Prim
Prim is the smallest logical container unit in USD. It serves as both a node in USD's hierarchical structure and the object of Reference operations (similar to a folder in a file system or an instance in object-oriented programming). The roles of Prim are as follows:

* Prim forms the tree structure of a USD scene. Prim can contain child Prims, forming path identifiers (SdfPath) such as `/World/Characters/Hero`. This hierarchy can be extended across files through Reference, for example, referencing the `/Character` Prim from an external "character.usd" file as a child node of `/World/Characters`.
* Prim can represent any 3D scene entity, including:
   * Specific scene elements: Entities that directly constitute the visual or physical properties of a scene, including geometry (Mesh, Cube), light sources (SphereLight), cameras (UsdGeomCamera), and so on.
   * Abstract structures: Logical containers used to organize and transform scene entities, including Xform and Scope:
      * Xform: A Prim with transformation properties (translation, rotation, scaling). After being referenced, its spatial position can be independently adjusted, enabling multi-instance layouts. For example, referencing the Xform Prim of the same chair four times and placing it around a table.
      * Scope: Purely logical grouping Prim, with no transform attributes, suitable for integrating multiple Prims without spatial transformation requirements via Reference. For example, referencing the `/Props` Scope Prim from "PropsCollection.usd" as a child node of `/World`.
   * Supported components: Functional entities that provide appearance or animation capabilities to scene entities, including material (UsdPreviewSurface), skeleton, and more.

Prim is defined and associated through the following behaviors:

* Attributes: Store numerical data (such as size and color), and support animation keyframes.
* Relationships: Establish logical connections between Prims. Reference is essentially a special relationship that points to an external Prim (for example, referencing the `/Chair` Prim as a child node of `/Table`).
* Metadata: Additional information that controls Prim behavior, such as `active` (whether active) and `hidden` (whether hidden).

## USD reuse mechanism: Reference
Reference non-destructively combines Prims from external USD files into the current spatial container, used to connect scattered Prim resources. The functions of Reference are as follows:

* Reference allows one or more Prims from USD files to be referenced at any hierarchy within the current spatial container, enabling resource reuse. For example: referencing the `/Chair` Prim from "Chair.usd" four times to quickly build a "one table, four chairs" scene, with each referenced Prim remaining independently editable.
* The data of referenced Prims can be overridden in the current layer (or a layer with higher priority), such as modifying position, material, or animation, without affecting the original USD file. This allows the same referenced Prim to generate multiple variant instances (such as "red chair" and "blue chair").

Reference has the following characteristics:

* Flexible number of references: A Prim may contain no references, or may reference one or more external Prims simultaneously. You can add, delete, or modify these references at any time to dynamically adjust the composition of the scene.
* Memory and performance optimization: When you reference the same USD file multiple times, its content is loaded into memory only once to save resources. Meanwhile, each reference instance is independent, so modifying one instance (for example, changing the color of one of four chairs) does not affect the other instances.
* Safe against circular references: USD is designed to prevent circular dependencies. For example, a scene can contain a structure where A references B and B references C, but the system prevents cycles such as A referencing B while B also references A.

## Application scenarios
The application scenarios for USD are as follows:

* Large-scale scene assembly: Independently created USD assets (including Prim), such as buildings, vehicles, and vegetation, can be quickly assembled into open world environments by referencing them through Reference.
* Multi-instance asset management: To create both dirty and clean appearance versions of the same vehicle, simply override the material in the Reference to achieve multi-instance and variant effects.
* Cross-department collaboration: The layout, modeling, and animation departments work based on the same set of Prim references, enabling non-destructive modifications without interfering with each other and improving collaboration efficiency.


