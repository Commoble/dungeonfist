package com.github.commoble.dungeonfist.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.stream.JsonReader;

public class NameWords
{
	public static final List<String> generic_nouns = new ArrayList<String>();
	public static final List<String> generic_prefixes = new ArrayList<String>();
	public static final List<String> weapon_suffixes = new ArrayList<String>();
	public static void importNameWords(Class<?> clazz)
	{
		
		InputStream stream = clazz.getClassLoader().getResourceAsStream("data/dungeonfist/name_words.json");
		JsonReader reader = null;
		
		try
		{
			reader = new JsonReader(new InputStreamReader(stream, "UTF-8"));
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
		reader.beginObject();
		
		HashMap<String, String> jsonObject = new HashMap<>();
		while (reader.hasNext()) // next key in object
		{
			String typeKey = reader.nextName();
			if (typeKey.equals("generic"))
			{
				reader.beginObject();
				
				while(reader.hasNext())
				{
					String grammarKey = reader.nextName();
					if (grammarKey.equals("nouns"))
					{
						reader.beginArray();
						
						while(reader.hasNext())
						{
							generic_nouns.add(reader.nextString());
						}

						reader.endArray();
					}
					else if (grammarKey.equals("prefixes"))
					{
						reader.beginArray();
						
						while(reader.hasNext())
						{
							generic_prefixes.add(reader.nextString());
						}

						reader.endArray();
					}
				}

				reader.endObject();
			}
			else if (typeKey.equals("weapon"))
			{
				reader.beginObject();
				
				while(reader.hasNext())
				{
					String grammarKey = reader.nextName();
					if (grammarKey.equals("suffixes"))
					{
						reader.beginArray();
						
						while(reader.hasNext())
						{
							weapon_suffixes.add(reader.nextString());
						}

						reader.endArray();
					}
				}

				reader.endObject();
			}
		}

		reader.endObject();
	}
}
