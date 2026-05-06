* **Shared space in the SDK's Planar and Volumetric ** modes:**
   * The total number of triangle faces does not exceed 175,000.
   * Whenever possible, use textures with a resolution of 2048×2048 or lower. When using the editor, apply the default ASTC6x6 compression to reduce package size and speed up loading.
   * Keep drawcalls at 15 or fewer.
   * Keep the number of Activity Entities at 30 or fewer.
   * It is recommended to keep the number of skeletal nodes in SkeletonMeshEntity at 72 or fewer, and ensure that no single vertex is influenced by more than 4 weighted bones.
* **Exclusive Space (Stage mode in the SDK):**
   * The total number of triangle faces does not exceed 350,000.
   * Textures should preferably use a resolution of 4096x4096 or lower. When using the editor, apply the default ASTC6x6 compression to optimize package size and improve loading speed.
   * Keep drawcalls at 30 or fewer.
   * Keep the number of Activity Entities at 60 or fewer.
   * It is recommended to keep the number of bone nodes in SkeletonMeshEntity at 120 or fewer, and the number of bones influencing each vertex must not exceed four.
* **When creating EXR resources for Image Base Light:**
   * Set the texture to R11F_G11F_B10F.
   * Caution: If you need to provide environment maps directly in the assets directory of the project IDE for lighting, you must convert them to ktx format, and the resolution should ideally be kept at 256 or lower.
   * The editor can directly use environment maps in EXR and HDR formats. When outputting through a component, the editor will automatically and forcibly convert them to KTX textures with a 256-pixel resolution. Therefore, during the art production phase, it is also not necessary to provide environment maps with excessively high resolution. It is recommended to use environment maps with a resolution from 2K to 1K. To ensure the correct effect before and after conversion.
