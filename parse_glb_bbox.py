import json
import struct
import math

def parse_glb(file_path):
    with open(file_path, 'rb') as f:
        header = f.read(12)
        magic, version, length = struct.unpack('<4sII', header)
        
        chunk_header = f.read(8)
        chunk_length, chunk_type = struct.unpack('<II', chunk_header)
        
        if chunk_type == 0x4E4F534A: # 'JSON'
            json_data = f.read(chunk_length).decode('utf-8')
            gltf = json.loads(json_data)
            
            for i, mesh in enumerate(gltf.get('meshes', [])):
                print(f"Mesh {i}: {mesh.get('name', '')}")
                for prim in mesh.get('primitives', []):
                    pos_acc_idx = prim.get('attributes', {}).get('POSITION')
                    if pos_acc_idx is not None:
                        acc = gltf['accessors'][pos_acc_idx]
                        print(f"  Min: {acc.get('min')}, Max: {acc.get('max')}")

parse_glb('app/src/main/assets/pico_robot_animated.glb')
