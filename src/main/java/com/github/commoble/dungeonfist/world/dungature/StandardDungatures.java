package com.github.commoble.dungeonfist.world.dungature;

public class StandardDungatures
{
	public static DungatureTable table = new DungatureTable();
	static
	{
		table.add(1, 1, new ColumnDungature());
	}
}
