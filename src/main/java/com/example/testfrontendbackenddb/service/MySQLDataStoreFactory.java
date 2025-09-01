package com.example.testfrontendbackenddb.service;

import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.gson.Gson;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;

@Component
public class MySQLDataStoreFactory implements DataStoreFactory {

    private final JdbcTemplate jdbcTemplate;
    private final Gson gson = new Gson();

    public MySQLDataStoreFactory(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <V extends Serializable> DataStore<V> getDataStore(String id) {
        return new DataStore<V>() {
            @Override
            public DataStore<V> set(String key, V value) {
                String json = gson.toJson(value);
                jdbcTemplate.update(
                        "INSERT INTO oauth_tokens (user_id, token_json) VALUES (?, ?) ON DUPLICATE KEY UPDATE token_json=?",
                        key, json, json);
                return this;
            }

            @Override
            public V get(String key) {
                List<String> results = jdbcTemplate.queryForList(
                        "SELECT token_json FROM oauth_tokens WHERE user_id = ?", String.class, key);
                if (results.isEmpty()) return null;
                return (V) gson.fromJson(results.get(0), StoredCredential.class);
            }

            @Override public DataStore<V> delete(String key) { jdbcTemplate.update("DELETE FROM oauth_tokens WHERE user_id = ?", key); return this; }
            @Override public DataStoreFactory getDataStoreFactory() { return MySQLDataStoreFactory.this; }
            @Override public String getId() { return id; }
            @Override public int size() { Integer c = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM oauth_tokens", Integer.class); return c==null?0:c; }
            @Override public boolean isEmpty() { return size() == 0; }
            @Override public boolean containsKey(String key) { Integer c = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM oauth_tokens WHERE user_id = ?", Integer.class, key); return c!=null && c>0; }
            @Override public boolean containsValue(V value) { return false; }
            @Override public Set<String> keySet() { return new HashSet<>(jdbcTemplate.queryForList("SELECT user_id FROM oauth_tokens", String.class)); }
            @Override public Collection<V> values() { return Collections.emptyList(); }
            @Override public DataStore<V> clear() { jdbcTemplate.update("DELETE FROM oauth_tokens"); return this; }
        };
    }
}
