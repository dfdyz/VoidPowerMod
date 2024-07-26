
local Vec = dofile("vector.lua")

--Config
local controller_at = "left"
local yaw_off = -90 / 180 * math.pi


-- Init
--monitor = peripheral.find("monitor")
ship = peripheral.wrap(controller_at)
ctrl = peripheral.wrap("front")

ship.setIdle(false)

function P(str, at)
    --monitor.setCursorPos(1,at)
    --monitor.write(tostring(str))
end

function phyTick(physImpl)
    local data = physImpl.getInertia()
    local pose = physImpl.getPoseVel()

    local velocity = pose.velocity
    local velocity_num = Vec.Length(velocity)
    local _omega = Vec.Negative(pose.omega)
    --local pos = pose.pos
    --local rot = pose.rot
    local up_vec = pose.up

    local yaw = pose.yaw + yaw_off
    -- input
    local yaw_ctrl = ctrl.getAxis(1)
    local speed_ctrl = -ctrl.getAxis(2)

    local key_up = ctrl.getButton(12)
    local key_down = ctrl.getButton(14)
    local key_r = ctrl.getButton(7)

    if not ctrl.hasUser() then
        yaw_ctrl = 0
        speed_ctrl = 0
        key_up = false
        key_down = false
        key_r = true
    end

    local high_ctrl = 0
    if key_up then
        high_ctrl = high_ctrl+1
    end
    if key_down then
        high_ctrl = high_ctrl-1
    end

    -- gyro
    local torque = Vec.Normalize(Vec.Cross(up_vec, Vec.AxisY()))
    local angle = Vec.Angle(up_vec, Vec.AxisY()) * 40

    torque = Vec.MulNum(torque, angle)
    torque = physImpl.transformOmega(torque.x, torque.y, torque.z)
    ship.applyInvariantTorque(torque.x, torque.y, torque.z)
    --print(torque.x, torque.y, torque.z)

    local _torque = physImpl.transformOmega(_omega.x, _omega.y, _omega.z)
    _torque = Vec.MulNum(_torque, 10)
    ship.applyInvariantTorque(_torque.x, _torque.y, _torque.z)

    if yaw_ctrl ~= 0 then
        local tmp = math.max(velocity_num, 20) * 0.2
        torque = Vec.MulNum(ship.getFaceVector(), speed_ctrl * yaw_ctrl * tmp)
        torque = physImpl.transformOmega(torque.x, torque.y, torque.z)
        ship.applyInvariantTorque(torque.x, torque.y, torque.z)
    end

    -- yaw control
    torque = Vec.MulNum(Vec.AxisY(), -yaw_ctrl * 20)
    torque = physImpl.transformOmega(torque.x, torque.y, torque.z)
    ship.applyInvariantTorque(torque.x, torque.y, torque.z)


    -- hover power
    ship.applyInvariantForce(0, 10 * data.mass, 0)

    local damping = data.mass * 5

    if key_r then
        damping = damping * 3
    end

    local damping_force = Vec.MulNum(velocity, -damping)

    if speed_ctrl ~= 0 then
        damping_force.x = damping_force.x * 0.5
        damping_force.z = damping_force.z * 0.5
    end

    if high_ctrl then
        damping_force.y = damping_force.y * 0.2
    end


    ship.applyInvariantForce(
            damping_force.x,
            damping_force.y * 3,
            damping_force.z)

    -- movement power
    local f = Vec.Zero()
    f.y = data.mass * 50 * high_ctrl

    P(damping_force.x, 1)
    P(damping_force.z, 2)

    local acc = 50
    local fx = acc * math.sin(-yaw)
    local fz = acc * math.cos(-yaw)

    P(velocity.x, 3)
    P(velocity.z, 4)

    f.x = fx * data.mass * 4 * speed_ctrl
    f.z = fz * data.mass * 4 * speed_ctrl

    ship.applyInvariantForce(f.x, f.y, f.z)
end


function mainLoop()
    while true do
        local event, physImpl = os.pullEvent("phys_tick")
        phyTick(physImpl)
    end
end

mainLoop()
