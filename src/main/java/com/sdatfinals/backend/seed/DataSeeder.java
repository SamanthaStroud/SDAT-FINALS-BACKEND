package com.sdatfinals.backend.seed;

import com.sdatfinals.backend.concept.Concept;
import com.sdatfinals.backend.concept.ConceptRepository;
import com.sdatfinals.backend.topic.Topic;
import com.sdatfinals.backend.topic.TopicRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DataSeeder implements CommandLineRunner {

    private final TopicRepository topicRepository;
    private final ConceptRepository conceptRepository;

    public DataSeeder(TopicRepository topicRepository, ConceptRepository conceptRepository) {
        this.topicRepository = topicRepository;
        this.conceptRepository = conceptRepository;
    }

    @Override
    public void run(String... args) {
        if (topicRepository.count() > 0) {
            return; // already seeded, skip
        }

        Topic databases = new Topic();
        databases.setSlug("databases");
        databases.setName("Databases");
        databases.setDescription("Core database concepts every developer should know.");
        databases.setCategory("Backend");
        databases.setCategorySymbol("🗄️");
        databases.setColor("#336791");
        databases.setAccentColor("#4a90d9");
        databases.setBorderColor("#2c5a7a");
        topicRepository.save(databases);

        Concept normalization = new Concept();
        normalization.setTopic(databases);
        normalization.setSlug("normalization");
        normalization.setName("Normalization");
        normalization.setSimpleExplanation("Organizing data to reduce redundancy.");
        normalization.setGroup("Fundamentals");
        normalization.setTechnicalExplanation(
                "A process of structuring a relational database in accordance with normal forms.");
        normalization.setDiagram("");
        normalization.setGlance(List.of(
                "Reduces duplicate data",
                "Improves data integrity",
                "Involves normal forms (1NF, 2NF, 3NF)"
        ));
        normalization.setCommonMistakes(List.of(
                Map.of("title", "Over-normalizing", "desc", "Too many joins hurt performance",
                       "fix", "Balance normalization with query needs")
        ));
        normalization.setWhyItMatters(List.of(
                Map.of("icon", "💾", "title", "Data Integrity", "desc", "Prevents anomalies during updates")
        ));
        normalization.setCodeExamples(List.of(
                Map.of("label", "SQL Example", "filename", "schema.sql",
                       "code", "CREATE TABLE users (id SERIAL PRIMARY KEY, email TEXT UNIQUE);")
        ));
        normalization.setMiniChallenge(
                Map.of("title", "Normalize this table",
                       "description", "Given a flat table, split it into normalized form.",
                       "hints", List.of("Look for repeating groups", "Identify functional dependencies"))
        );
        conceptRepository.save(normalization);

        Concept indexing = new Concept();
        indexing.setTopic(databases);
        indexing.setSlug("indexing");
        indexing.setName("Indexing");
        indexing.setSimpleExplanation("Speeding up data lookups using index structures.");
        indexing.setGroup("Performance");
        indexing.setTechnicalExplanation(
                "An index is a data structure that improves the speed of data retrieval at the cost of extra writes and storage.");
        indexing.setDiagram("");
        indexing.setGlance(List.of(
                "Speeds up SELECT queries",
                "Slows down INSERT/UPDATE slightly",
                "Common types: B-tree, hash, GIN"
        ));
        indexing.setCommonMistakes(List.of(
                Map.of("title", "Indexing everything", "desc", "Too many indexes slow down writes",
                       "fix", "Only index columns used in WHERE/JOIN/ORDER BY frequently")
        ));
        indexing.setWhyItMatters(List.of(
                Map.of("icon", "⚡", "title", "Query Performance", "desc", "Turns slow table scans into fast lookups")
        ));
        indexing.setCodeExamples(List.of(
                Map.of("label", "Create an index", "filename", "index.sql",
                       "code", "CREATE INDEX idx_users_email ON users(email);")
        ));
        indexing.setMiniChallenge(
                Map.of("title", "Speed up this query",
                       "description", "Given a slow query on a large table, propose an index to fix it.",
                       "hints", List.of("Check the WHERE clause", "Check the JOIN condition"))
        );
        conceptRepository.save(indexing);
    }
}