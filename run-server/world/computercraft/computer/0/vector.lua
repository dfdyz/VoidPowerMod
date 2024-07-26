Vec = {}

function Vec.New(vx,vy,vz)
    return { x=vx, y=vy, z=vz }
end

function Vec.Dot(v1, v2)
    return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z
end

function Vec.Cross(v1, v2)
    local v3 ={x = v1.y*v2.z - v2.y*v1.z , y = v2.x*v1.z-v1.x*v2.z , z = v1.x*v2.y-v2.x*v1.y}
    return v3
end

function Vec.MulNum(v1, n)
    return {
        x = v1.x * n,
        y = v1.y * n,
        z = v1.z * n
    }
end

function Vec.Mul(v1, v2)
    return {
        x = v1.x * v2.x,
        y = v1.y * v2.y,
        z = v1.z * v2.z
    }
end

function Vec.Length(v)
    return math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z)
end

function Vec.Angle(v1, v2)
    local cos = Vec.Dot(v1, v2)/ (Vec.Length(v1)*Vec.Length(v2))
    return math.acos(cos)
end

function Vec.Normalize(v)
    local l = Vec.Length(v)
    if l == 0 then
        return Vec.Zero
    end
    return {x=v.x / l, y = v.y/l, z = v.z/l}
end

function Vec.Negative(v)
    return Vec.New(-v.x, -v.y, -v.z)
end

function Vec.Zero()
    return Vec.New(0,0,0)
end

function Vec.AxisX()
    return Vec.New(1,0,0)
end

function Vec.AxisY()
    return Vec.New(0,1,0)
end

function Vec.AxisZ()
    return Vec.New(0,0,1)
end

return Vec