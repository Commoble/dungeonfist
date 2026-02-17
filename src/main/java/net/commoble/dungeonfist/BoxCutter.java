package net.commoble.dungeonfist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public record BoxCutter(BoundingBox all, BoundingBox interior, Map<Direction, BoundingBox> sides, List<BoundingBox> wallEdges, List<BoundingBox> floorEdges, List<BoundingBox> ceilingEdges, Set<BlockPos> floorCorners, Set<BlockPos> ceilingCorners)
{
	public static boolean canCut(BoundingBox box)
	{
		return box.getXSpan() >= 3
			&& box.getYSpan() >= 3
			&& box.getZSpan() >= 3;
	}
	
	public static BoxCutter cut(BoundingBox box)
	{
		BoundingBox interior = box.inflatedBy(-1);
		int interiorMinX = interior.minX();
		int interiorMinY = interior.minY();
		int interiorMinZ = interior.minZ();
		int interiorMaxX = interior.maxX();
		int interiorMaxY = interior.maxY();
		int interiorMaxZ = interior.maxZ();
		Map<Direction,BoundingBox> sides = new HashMap<>();
		for (Direction side : Direction.values())
		{
			Direction.Axis axis = side.getAxis();
			int minX = switch(side) {
				case WEST -> box.minX();
				case EAST -> box.maxX();
				default -> interiorMinX;
			};
			int minY = switch(side) {
				case DOWN -> box.minY();
				case UP -> box.maxY();
				default -> interiorMinY;
			};
			int minZ = switch(side) {
				case NORTH -> box.minZ();
				case SOUTH -> box.maxZ();
				default -> interiorMinZ;
			};
			int maxX = axis == Axis.X ? minX : interiorMaxX;
			int maxY = axis == Axis.Y ? minY : interiorMaxY;
			int maxZ = axis == Axis.Z ? minZ : interiorMaxZ;
			sides.put(side, new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ));
		}
		
		List<BoundingBox> wallEdges = new ArrayList<>(4);
		for (int i=0; i<4; i++)
		{
			int wallX = i / 2 == 0 ? box.minX() : box.maxX();
			int wallZ = i % 2 == 0 ? box.minZ() : box.maxZ();
			wallEdges.add(new BoundingBox(wallX,interiorMinY, wallZ, wallX, interiorMaxY, wallZ));
		}
		List<BoundingBox> floorEdges = List.of(
			new BoundingBox(interiorMinX, box.minY(), box.minZ(), interiorMaxX, box.minY(), box.minZ()),
			new BoundingBox(interiorMinX, box.minY(), box.maxZ(), interiorMaxX, box.minY(), box.maxZ()),
			new BoundingBox(box.minX(), box.minY(), interiorMinZ, box.minX(), box.minY(), interiorMaxZ),
			new BoundingBox(box.maxX(), box.minY(), interiorMinZ, box.maxX(), box.minY(), interiorMaxZ));
		List<BoundingBox> ceilingEdges = List.of(
			new BoundingBox(interiorMinX, box.maxY(), box.minZ(), interiorMaxX, box.maxY(), box.minZ()),
			new BoundingBox(interiorMinX, box.maxY(), box.maxZ(), interiorMaxX, box.maxY(), box.maxZ()),
			new BoundingBox(box.minX(), box.maxY(), interiorMinZ, box.minX(), box.maxY(), interiorMaxZ),
			new BoundingBox(box.maxX(), box.maxY(), interiorMinZ, box.maxX(), box.maxY(), interiorMaxZ));
		Set<BlockPos> floorCorners = new HashSet<>();
		Set<BlockPos> ceilingCorners = new HashSet<>();
		for (int i=0; i<4; i++)
		{
			int x = i / 2 == 0 ? box.minX() : box.maxX();
			int z = i % 2 == 0 ? box.minZ() : box.maxZ();
			floorCorners.add(new BlockPos(x, box.minY(), z));
			ceilingCorners.add(new BlockPos(x, box.maxY(), z));
		}
		
		return new BoxCutter(box, interior, sides, wallEdges, floorEdges, ceilingEdges, floorCorners, ceilingCorners);
	}
	
	public List<BoundingBox> exterior()
	{
		List<BoundingBox> results = new ArrayList<>(26);
		for (BoundingBox box : this.sides.values())
		{
			results.add(box);
		}
		for (BoundingBox box : this.wallEdges)
		{
			results.add(box);
		}
		for (BoundingBox box : this.floorEdges)
		{
			results.add(box);
		}
		for (BoundingBox box : this.ceilingEdges)
		{
			results.add(box);
		}
		for (BlockPos pos : this.ceilingCorners)
		{
			results.add(new BoundingBox(pos));
		}
		for (BlockPos pos : this.floorCorners)
		{
			results.add(new BoundingBox(pos));
		}
		return results;
	}
	
	// TODO add more box sections as needed
	public static enum BoxSection implements StringRepresentable
	{
		ALL(box -> List.of(box.all())),
		INTERIOR(box -> List.of(box.interior)),
		TALL_INTERIOR(box -> List.of(
			box.interior(),
			box.sides.get(Direction.DOWN),
			box.sides.get(Direction.UP))),
		TUBE(box -> {
			List<BoundingBox> list = new ArrayList<>(8);
			list.add(box.sides.get(Direction.NORTH));
			list.add(box.sides.get(Direction.SOUTH));
			list.add(box.sides.get(Direction.WEST));
			list.add(box.sides.get(Direction.EAST));
			list.addAll(box.wallEdges);
			return list;
		}),
		TALL_TUBE(box -> {
			List<BoundingBox> list = new ArrayList<>(24);
			list.addAll(BoxSection.TUBE.getSections(box));
			list.addAll(box.floorEdges);
			list.addAll(box.ceilingEdges);
			list.addAll(box.floorCorners.stream().map(corner -> BoundingBox.fromCorners(corner, corner)).toList());
			list.addAll(box.ceilingCorners.stream().map(corner -> BoundingBox.fromCorners(corner, corner)).toList());
			return list;
		}),
		FLOOR(box -> List.of(box.sides.get(Direction.DOWN)));
		
		public static final Codec<BoxSection> CODEC = StringRepresentable.fromValues(BoxSection::values);
		
		private final Function<BoxCutter,List<BoundingBox>> sectionGetter;
		
		BoxSection(Function<BoxCutter,List<BoundingBox>> sectionGetter)
		{
			this.sectionGetter = sectionGetter;
		}

		@Override
		public String getSerializedName()
		{
			return this.name().toLowerCase(Locale.ROOT);
		}
		
		public List<BoundingBox> getSections(BoxCutter box)
		{
			return this.sectionGetter.apply(box);
		}
	}
}
