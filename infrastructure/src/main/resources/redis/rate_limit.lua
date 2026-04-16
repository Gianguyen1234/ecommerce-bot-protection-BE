local key = KEYS[1]
local now = tonumber(ARGV[1])
local window = tonumber(ARGV[2])

-- remove old
redis.call("ZREMRANGEBYSCORE", key, 0, now - window)

-- dùng member unique
local member = now .. "-" .. math.random()

redis.call("ZADD", key, now, member)

local count = redis.call("ZCARD", key)

redis.call("EXPIRE", key, window)

return count