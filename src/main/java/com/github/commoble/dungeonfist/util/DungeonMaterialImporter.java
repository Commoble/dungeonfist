package com.github.commoble.dungeonfist.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.function.Function;

import com.google.gson.stream.JsonReader;

import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class DungeonMaterialImporter
{
	public static void importDungeonMaterialJson(Class<?> clazz)
	{
		
		InputStream matStream = clazz.getClassLoader().getResourceAsStream("data/dungeonfist/dungeon_materials.json");
		JsonReader reader = null;
		
		try
		{
			reader = new JsonReader(new InputStreamReader(matStream, "UTF-8"));
			readJson(reader);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				reader.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		
		
	}
	
	private static void readJson(JsonReader reader) throws IOException
	{
		reader.beginArray();

		while(reader.hasNext()) // next object in array
		{
			reader.beginObject();
			
			HashMap<String, String> jsonObject = new HashMap<>();
			
			while (reader.hasNext()) // next key in object
			{
				String key = reader.nextName();
				String blockID = reader.nextString();
				jsonObject.put(key, blockID);
			}
			
			Function<String, Block> finder = key -> {
				try
				{
					String blockID = jsonObject.get(key);
					if (blockID == null)
					{
						throw new IOException("Bad json file at " + reader.getPath() +" --- missing key of " + key);
					}
					Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(blockID));
					if (block == null)
					{
						throw new IOException("Bad json file at " + reader.getPath() + " --- could not find block for " + blockID);
					}
					return block;
				}
				catch (IOException e)
				{
					e.printStackTrace();
					return null;
				}
				
			};
			
			Block base = finder.apply("base");
			Block damaged = finder.apply("damaged");
			Block mossy = finder.apply("mossy");
			Block stairs = finder.apply("stairs");
			Block wall = finder.apply("wall");
			Block plate = finder.apply("plate");
			Block slab = finder.apply("slab");
			
			if (!(stairs instanceof StairsBlock))
			{
				throw new IOException("Dungeon material key 'stairs' for baseblock " + base.getRegistryName() + " refers to " + stairs.getRegistryName() + " which is not of type StairsBlock");
			}
			if (!(wall instanceof WallBlock))
			{
				throw new IOException("Dungeon material key 'wall' for baseblock " + base.getRegistryName() + " refers to " + wall.getRegistryName() + " which is not of type WallBlock");
			}
			if (!(slab instanceof SlabBlock))
			{
				throw new IOException("Dungeon material key 'slab' for baseblock " + base.getRegistryName() + " refers to " + slab.getRegistryName() + " which is not of type SlabBlock");
			}
			
			DungeonMaterials.dungeonMaterials.add(base);
			DungeonMaterials.damagedMap.put(base, damaged);
			DungeonMaterials.mossyMap.put(base, mossy);
			DungeonMaterials.plateMap.put(base, plate);
			DungeonMaterials.stairMap.put(base, (StairsBlock)stairs);
			DungeonMaterials.wallMap.put(base, (WallBlock)wall);
			DungeonMaterials.slabMap.put(base, (SlabBlock)slab);
			
			reader.endObject();
		}
		reader.endArray();
	}
}
