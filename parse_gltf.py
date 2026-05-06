import json
import struct

def parse_glb(file_path):
    with open(file_path, 'rb') as f:
        header = f.read(12)
        magic, version, length = struct.unpack('<4sII', header)
        chunk_header = f.read(8)
        chunk_length, chunk_type = struct.unpack('<II', chunk_header)
        if chunk_type == 0x4E4F534A: # 'JSON'
            json_data = f.read(chunk_length).decode('utf-8')
            gltf = json.loads(json_data)
            
            print("Accessors:")
            for i, acc in enumerate(gltf.get('accessors', [])):
                if acc.get('type') == 'VEC3' and acc.get('componentType') == 5126: # Float
                    print(f"  Acc {i}: min={acc.get('min')}, max={acc.get('max')}")

parse_glb('app/src/main/assets/pico_robot_animated.glb')
