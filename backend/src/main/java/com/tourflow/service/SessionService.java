package com.tourflow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class SessionService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Récupère une valeur de session depuis Redis
     */
    public Object getSessionValue(String sessionId, String key) {
        String redisKey = "spring:session:sessions:" + sessionId + ":sessionAttr:" + key;
        return redisTemplate.opsForValue().get(redisKey);
    }

    /**
     * Définit une valeur de session dans Redis
     */
    public void setSessionValue(String sessionId, String key, Object value) {
        String redisKey = "spring:session:sessions:" + sessionId + ":sessionAttr:" + key;
        redisTemplate.opsForValue().set(redisKey, value, 1, TimeUnit.HOURS); // TTL de 1 heure
    }

    /**
     * Supprime une valeur de session de Redis
     */
    public void removeSessionValue(String sessionId, String key) {
        String redisKey = "spring:session:sessions:" + sessionId + ":sessionAttr:" + key;
        redisTemplate.delete(redisKey);
    }

    /**
     * Récupère toutes les clés de session pour un utilisateur
     */
    public Set<String> getSessionKeys(String sessionId) {
        String pattern = "spring:session:sessions:" + sessionId + ":sessionAttr:*";
        return redisTemplate.keys(pattern);
    }

    /**
     * Invalide une session complète
     */
    public void invalidateSession(String sessionId) {
        String sessionKey = "spring:session:sessions:" + sessionId;
        Collection<String> keys = redisTemplate.keys(sessionKey + "*");
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * Vérifie si une session existe
     */
    public boolean sessionExists(String sessionId) {
        String sessionKey = "spring:session:sessions:" + sessionId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(sessionKey));
    }

    /**
     * Étend la durée de vie d'une session
     */
    public void extendSession(String sessionId) {
        String sessionKey = "spring:session:sessions:" + sessionId;
        redisTemplate.expire(sessionKey, 1, TimeUnit.HOURS);
    }
}
