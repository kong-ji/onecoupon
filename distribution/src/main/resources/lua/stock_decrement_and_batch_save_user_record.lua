-- 定义最大值和位数
local SECOND_FIELD_BITS = 13
--表示用 13 位来存储 第二字段的值。在脚本中，两个字段（布尔值 firstField 和整数 secondField）被组合为一个整数返回。
-- 为什么是13呢，因为一次操作5000条数据, 2^12<5000<2^13
--firstField：表示库存是否大于 0，true 为有库存，false 为无库存。
--secondField：表示当前用户领取集合的大小。
--这种位运算方式可以节省网络传输和存储开销，同时返回多个有意义的值。

-- 将两个字段组合成一个int
local function combineFields(firstField, secondField)
    local firstFieldValue = firstField and 1 or 0
    return (firstFieldValue * 2 ^ SECOND_FIELD_BITS) + secondField
                    --    (1 * 2^13) + 1000 = 8192 + 1000 = 9192
end

-- Lua脚本开始
local key = KEYS[1] -- Redis Key
local userSetKey = KEYS[2] -- 用户领券 Set 的 Key
local userIdAndRowNum = ARGV[1] -- 用户 ID 和 Excel 所在行数

-- 获取库存
local stock = tonumber(redis.call('HGET', key, 'stock'))

-- 检查库存是否大于0
if stock == nil or stock <= 0 then
    --库存不足,返回false和旧的优惠券集合长度
    return combineFields(false, redis.call('SCARD', userSetKey))
end

-- 自减库存
redis.call('HINCRBY', key, 'stock', -1)

-- 添加用户到领券集合
redis.call('SADD', userSetKey, userIdAndRowNum)

-- 获取用户领券集合的长度
local userSetLength = redis.call('SCARD', userSetKey)

-- 返回结果
return combineFields(true, userSetLength)
