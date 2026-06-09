# 混合推进器推力分配的 SOCP 求解

## 1. 问题描述

给定一艘刚体飞船，其重心（CG）位置为 \(\mathbf{c} \in \mathbb{R}^3\)，装有 \(N\) 个推进器，每个推进器 \(i\) 的属性：

| 属性 | 符号 | 说明 |
|------|------|------|
| 安装位置（绝对坐标） | \(\mathbf{p}_i \in \mathbb{R}^3\) | 推进器在全局坐标系中的安装位置 |
| 原始方向 | \(\mathbf{d}_i \in \mathbb{R}^3\) | 单位向量 |
| 可旋转 | \(r_i \in \{\text{true}, \text{false}\}\) | 能否在半球内改变方向 |
| 最大推力 | \(F_{i,\max} > 0\) |

重心到推进器的力臂：\(\mathbf{r}_i = \mathbf{p}_i - \mathbf{c}\)

需选择合适的推力矢量 \(\mathbf{v}_i \in \mathbb{R}^3\)（可旋转型）或标量 \(\alpha_i \ge 0\)（固定型），使得：

- 合力 \(\sum \mathbf{v}_i = \mathbf{F}_{\text{target}}\)
- 合力矩 \(\sum (\mathbf{r}_i \times \mathbf{v}_i) = \mathbf{T}_{\text{target}}\)
- 各推进器不超出物理限制
- 推力总和最小

---

## 2. 推进器类型

### 可旋转推进器（\(R\) 个）

输出三维推力矢量 \(\mathbf{v}_i \in \mathbb{R}^3\)，受限于：

- **半球约束**：\(\mathbf{v}_i \cdot \mathbf{d}_i \ge 0\)（与原始方向夹角 \(\le 90^\circ\)）
- **推力上限**：\(\|\mathbf{v}_i\| \le F_{i,\max}\)

引入辅助变量 \(t_i\)，将 \(\|\mathbf{v}_i\| \le F_{i,\max}\) 拆分为二阶锥约束和线性约束：

\[
\|\mathbf{v}_i\| \le t_i \le F_{i,\max}
\]

### 固定推进器（\(F\) 个）

推力方向固定为 \(\mathbf{d}_i\)，仅标量大小可调：

\[
\mathbf{v}_i = \alpha_i \cdot \mathbf{d}_i,\quad \alpha_i \ge 0,\quad \alpha_i \le F_{i,\max}
\]

---

## 3. 二阶锥规划（SOCP）形式

### 3.1 决策变量

\[
\mathbf{x} = [\underbrace{\mathbf{v}_{0,x},\mathbf{v}_{0,y},\mathbf{v}_{0,z},\dots}_{3R},\;
               \underbrace{t_0,\dots,t_{R-1}}_{R},\;
               \underbrace{\alpha_0,\dots,\alpha_{F-1}}_{F}]^\mathsf{T}
\]

变量总数：\(n = 4R + F\)

### 3.2 锥结构

锥变量总数：\(m = 6R + 2F\)

| 锥类型 | 维度 | 对应约束 |
|--------|------|----------|
| LP 锥 | \(2R\) | 可旋转：\(\mathbf{v}_i \cdot \mathbf{d}_i \ge 0\)（\(R\)） + \(t_i \le F_{i,\max}\)（\(R\)） |
| LP 锥 | \(2F\) | 固定：\(\alpha_i \ge 0\)（\(F\)） + \(\alpha_i \le F_{i,\max}\)（\(F\)） |
| SOC 锥 | \(4R\)（\(R\) 个，每个 \(\text{dim}=4\)） | \(\|\mathbf{v}_i\| \le t_i\) |

### 3.3 约束矩阵

#### G 矩阵（锥约束，\(m \times n\)）

CCS 格式，每个变量列恰好 2 个非零元：

| 变量 | 非零元位置 | 值 |
|------|-----------|-----|
| 可旋转 \(\mathbf{v}_{i,x}\) | 行 \(i\)（半球） + 行 \((2R+2F+4i+1)\)（SOC） | \(-d_{i,x},\; -1.0\) |
| 可旋转 \(\mathbf{v}_{i,y}\) | 行 \(i\) + 行 \((2R+2F+4i+2)\) | \(-d_{i,y},\; -1.0\) |
| 可旋转 \(\mathbf{v}_{i,z}\) | 行 \(i\) + 行 \((2R+2F+4i+3)\) | \(-d_{i,z},\; -1.0\) |
| 可旋转 \(t_i\) | 行 \((R+i)\)（上限） + 行 \((2R+2F+4i)\)（SOC） | \(+1.0,\; -1.0\) |
| 固定 \(\alpha_j\) | 行 \((2R+j)\)（\(\ge 0\)） + 行 \((2R+F+j)\)（\(\le F_{\max}\)） | \(-1.0,\; +1.0\) |

#### A 矩阵（等式约束，\(6 \times n\)）

\[
A\mathbf{x} = \mathbf{b},\quad
\mathbf{b} = [F_{\text{target},x}, F_{\text{target},y}, F_{\text{target},z},
             T_{\text{target},x}, T_{\text{target},y}, T_{\text{target},z}]^\mathsf{T}
\]

**可旋转推进器 \(i\)（\(3\) 个变量列，每列 3 个非零元）：**

| 列 | \(A_{0,:}\) (F_x) | \(A_{1,:}\) (F_y) | \(A_{2,:}\) (F_z) | \(A_{3,:}\) (T_x) | \(A_{4,:}\) (T_y) | \(A_{5,:}\) (T_z) |
|----|----|----|----|----|----|----|
| \(v_{i,x}\) | \(1\) | | | | \(r_z\) | \(-r_y\) |
| \(v_{i,y}\) | | \(1\) | | \(-r_z\) | | \(r_x\) |
| \(v_{i,z}\) | | | \(1\) | \(r_y\) | \(-r_x\) | |

\(t_i\) 列全零。

**固定推进器 \(j\)（1 个变量列，6 个非零元）：**

\[
A_{:,4R+j} =
\begin{bmatrix}
d_{j,x} \\ d_{j,y} \\ d_{j,z} \\
r_{j,y}d_{j,z} - r_{j,z}d_{j,y} \\
r_{j,z}d_{j,x} - r_{j,x}d_{j,z} \\
r_{j,x}d_{j,y} - r_{j,y}d_{j,x}
\end{bmatrix}
\]

#### c 向量（目标）

\[
\mathbf{c} = [\underbrace{0,\dots,0}_{3R},\;
               \underbrace{1,\dots,1}_{R+F}]^\mathsf{T}
\]

#### h 向量（锥 RHS）

可旋转：\(h_i = 0\)（半球），\(h_{R+i} = F_{i,\max}\)（上限），SOC 部分全 0。

固定：\(h_{2R+j} = 0\)（\(\alpha \ge 0\)），\(h_{2R+F+j} = F_{j,\max}\)（上限）。

---

## 4. 重心变化

推进器位置 \(\mathbf{p}_i\)（绝对坐标）和重心 \(\mathbf{c}\)（绝对坐标）由外部更新。

每次求解时内力臂由下式实时计算：

\[
\mathbf{r}_i = \mathbf{p}_i - \mathbf{c}
\]

### A 矩阵中受影响的条目

推进器位置变化只影响力矩行（\(A_{3:6,:}\)）。力矩行使用 \(\mathbf{r}_i \times \mathbf{d}_i\)（固定型）或 \(\mathbf{r}_i \times \mathbf{v}_i\)（可旋转型，求解后确定）。

更新策略：写入新的原始（未经均衡的）Apr 值到独立缓冲区 `aprUpdateBuf`，调用 `ECOS_updateData`。

### ECOS_updateData 指针匹配优化

```c
if (/* 所有 5 组指针地址都与当前持有的指针不同 */) {
    unset_equilibration(w);  // 逆转旧均衡
}
// 更新指针
// set_equilibration(w)  // 重新均衡（始终执行）
```

Apr 缓冲区地址与求解器持有的不同 → 跳过 `unset_equilibration`，`set_equilibration` 对新 raw 值正确应用均衡。其余未变的 Gpr/c/h/b 地址相同，均衡因子 ≈ 1，数值几乎不变。

---

## 5. 方向变化

推进器方向 \(\mathbf{d}_i\) 可在运行时更新，影响：

| 影响范围 | 可旋转型 | 固定型 |
|----------|----------|--------|
| G 矩阵（半球行） | \(\mathbf{v}_i \cdot \mathbf{d}_i \ge 0\) 的系数 \(-d_{i,x/y/z}\) | 无影响 |
| A 矩阵（等式约束） | 无影响（方向由求解器决定） | 全部 6 个条目（力 + 力矩） |

更新策略：方向存储在 `dirs[i]` 中（持有 Thrust.getDir() 返回的 Vector3d 引用）。每次 `solve()` 将当前方向写入：

- **Gpr 缓冲区**：可旋转推进器的半球条目（每推进器 3 个 double 写入）
- **aprUpdateBuf**：固定推进器的全部 6 个条目（由 `updateAprRaw` 在写入位置时连带完成，因为力矩计算 \(r \times d\) 使用当前 dir）

---

## 6. 最大推力变化

`Thrust.Fmax` 可运行时修改。每次 `solve()` 将当前值写入 h 缓冲区对应位置（`h[R+rotOf[i]]` 或 `h[2R+F+fixOf[i]]`）。

---

## 7. JNI 绑定设计

### 7.1 数组传递策略

所有数组通过 `java.nio.ByteBuffer`（`allocateDirect`）传递，JNI 侧通过 `GetDirectBufferAddress` 提取指针。

```java
// Java 侧
ByteBuffer buf = ByteBuffer.allocateDirect(len * 8).order(ByteOrder.nativeOrder());
buf.asDoubleBuffer().put(data);

// JNI 侧
pfloat* ptr = (pfloat*)(*env)->GetDirectBufferAddress(env, buf);
```

### 7.2 字节序

`NewDirectByteBuffer` 创建的 Buffer 默认 `BIG_ENDIAN`（Java 标准），但 Windows x64 为 `LITTLE_ENDIAN`。JNI 侧调用 `.order(nativeOrder())` 修正，否则写入/读取的 double 值完全错误。

### 7.3 零拷贝解读取

`solve()` 后使用 `NewDirectByteBuffer` 包装求解器内部 \(w \to x\) 指针，Java 侧通过 `asDoubleBuffer()` 读取。无数据拷贝。求解器 `close()` 后该内存释放，Buffer 失效。

### 7.4 均衡数据原地修改

`ECOS_setup` 和 `ECOS_updateData` 内部的 `set_equilibration` 在传入的 ByteBuffer 上原地修改数据。所有传入 Buffer 在调用后不再包含原始数据。

---

## 8. 接口定义

### Ship（接口）

```java
public interface Ship {
    Vector3d getCenter();      // 重心（绝对坐标）
    Vector3d getTargetF();     // 目标力
    Vector3d getTargetT();     // 目标力矩
    List<Thrust> getThrusts(); // 推进器列表
}
```

### Thrust（接口）

```java
public interface Thrust {
    Vector3d getPos();         // 安装位置（绝对坐标）
    Vector3d getDir();         // 原始方向（单位向量）
    boolean  canRotate();      // 是否可旋转
    double   getFmax();        // 最大推力
    void     setFmax(double);
    Vector3d getForce();       // 求解结果（写入至此）
}
```

### ShipData / ThrustData

默认实现类，使用 `org.joml.Vector3d`。

### ThrustAllocator

```java
ThrustAllocator(Ship ship)           // 构造，读取推进器列表

void solve()                         // 无参求解，从 ship.targetF/T 读取目标
void solve(Vector3d F, Vector3d T)   // 显式指定目标

int lastExitFlag;                    // 最近一次求解的退出码
```

---

## 9. 求解流程

```
ship: thrusts (pos, dir, canRotate, Fmax), center, targetF, targetT
  │
  ├── initial solve()
  │     │
  │     ├── leverArms()        r_i = pos - center
  │     ├── refreshDir()       写入 Gpr（可旋转半球行）
  │     ├── updateAprRaw()    写入 aprUpdateBuf（力矩 + 固定推进器方向）
  │     ├── refreshFmax()     写入 h 缓冲
  │     ├── write b            写入 F_target, T_target
  │     ├── ECOS_updateData   跳过 unset_equil, 重均衡 + KKT 更新
  │     └── ECOS_solve         求解，结果写入 thrust.force
  │
  ├── ship.center changed
  │     └── next solve() → leverArms() 使用新 center 重算力臂
  │
  ├── thrust.dir / Fmax changed
  │     └── next solve() → refreshDir / refreshFmax 使用新值
  │
  └── ship.targetF/T changed
        └── next solve() → 新目标

每次 solve() 约 100-300 μs（4 推进器）
```
