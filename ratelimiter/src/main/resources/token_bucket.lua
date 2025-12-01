-- token_bucket.lua
-- KEYS[1] = key
-- ARGV[1] = capacity
-- ARGV[2] = refill_per_second
-- ARGV[3] = now_millis
-- ARGV[4] = cost

local key = KEYS[1]
local capacity = tonumber(ARGV[1])
local rps = tonumber(ARGV[2])
local now = tonumber(ARGV[3])
local cost = tonumber(ARGV[4])

local TOKENS_FIELD = "tokens"
local TS_FIELD = "ts"

local data = redis.call("HMGET", key, TOKENS_FIELD, TS_FIELD)
local tokens = tonumber(data[1])
local last_ts = tonumber(data[2])

if tokens == nil or last_ts == nil then
    tokens = capacity
    last_ts = now
end

local delta_ms = now - last_ts
if delta_ms < 0 then delta_ms = 0 end
local refill = (delta_ms / 1000.0) * rps
tokens = math.min(capacity, tokens + refill)
last_ts = now

local allowed = 0
local tokens_left = tokens

if tokens >= cost then
    allowed = 1
    tokens_left = tokens - cost
end

redis.call("HMSET", key, TOKENS_FIELD, tokens_left, TS_FIELD, last_ts)

local ttlSeconds = math.ceil((capacity / math.max(1.0, rps)) * 2)
if ttlSeconds < 60 then ttlSeconds = 60 end
redis.call("PEXPIRE", key, ttlSeconds * 1000)

return {tostring(allowed), tostring(tokens_left), tostring(ttlSeconds * 1000)}
