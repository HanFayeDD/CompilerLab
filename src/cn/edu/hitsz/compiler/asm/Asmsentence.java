package cn.edu.hitsz.compiler.asm;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Asmsentence {
    // 基本元素
    private final AsmKind kind;
    private final AsmvalReg result;
    private final List<Asmvalue> parts;
    //基建设施
    @Override
    public String toString() {
        if (kind == null) {
            return ".text";
        }
        return "   "+ kind.toString() + " " + result.toString() + ", " + parts.stream().map(Object::toString).collect(Collectors.joining(", "));
    }

    public static Asmsentence getText(){
        return new Asmsentence(null, null, null);
    }

    // 私有构造方法
    private Asmsentence(AsmKind kind, AsmvalReg result, List<Asmvalue> parts) {
        this.kind = kind;
        this.result = result;
        this.parts = parts;
    }

    // 静态方法
    public static Asmsentence createadd(AsmKind kind, AsmvalReg result, Asmvalue part1, Asmvalue part2) {
        if (!(part1 instanceof AsmvalReg && part2 instanceof AsmvalReg)) {
            throw new IllegalArgumentException("part1 and part2 must be AsmvalReg");
        }
        return new Asmsentence(kind, result, List.of(part1, part2));
    }

    public static Asmsentence createaddi(AsmKind kind, AsmvalReg result, Asmvalue part1, Asmvalue part2) {
        if (!(part1 instanceof AsmvalReg && part2 instanceof AsmvalImm)) {
            throw new IllegalArgumentException("part1 must be AsmvalReg, part2 must be Asmint");
        }
        return new Asmsentence(kind, result, List.of(part1, part2));
    }

    public static Asmsentence createsub(AsmKind kind, AsmvalReg result, Asmvalue part1, Asmvalue part2) {
        if (!(part1 instanceof AsmvalReg && part2 instanceof AsmvalReg)) {
            throw new IllegalArgumentException("part1 and part2 must be AsmvalReg");
        }
        return new Asmsentence(kind, result, List.of(part1, part2));
    }

    public static Asmsentence createsubi(AsmKind kind, AsmvalReg result, Asmvalue part1, Asmvalue part2) {
        if (!(part1 instanceof AsmvalReg && part2 instanceof AsmvalImm)) {
            throw new IllegalArgumentException("part1 must be AsmvalReg, part2 must be Asmint");
        }
        return new Asmsentence(kind, result, List.of(part1, part2));
    }

    public static Asmsentence createmul(AsmKind kind, AsmvalReg result, Asmvalue part1, Asmvalue part2) {
        if (!(part1 instanceof AsmvalReg && part2 instanceof AsmvalReg)) {
            throw new IllegalArgumentException("part1 and part2 must be AsmvalReg");
        }
        return new Asmsentence(kind, result, List.of(part1, part2));
    }

    public static Asmsentence createli(AsmKind kind, AsmvalReg result, Asmvalue part1) {
        if (!(part1 instanceof AsmvalImm)) {
            throw new IllegalArgumentException("part1 must be Asmint");
        }
        return new Asmsentence(kind, result, List.of(part1));
    }

    public static Asmsentence createmv(AsmKind kind, AsmvalReg result, Asmvalue part1) {
        if (!(part1 instanceof AsmvalReg)) {
            throw new IllegalArgumentException("part1 must be AsmvalReg");
        }
        return new Asmsentence(kind, result, List.of(part1));
    }

    public static void main(String[] args) {
        var temp = Asmsentence.createmv(AsmKind.mv, new AsmvalReg(Reg.a0), new AsmvalReg(Reg.t0));
        System.out.println(temp);
        var tq =   Asmsentence.createaddi(AsmKind.addi, new AsmvalReg(Reg.t0), new AsmvalReg(Reg.t2), new AsmvalImm(0));
        System.out.println(tq);
    }
}
