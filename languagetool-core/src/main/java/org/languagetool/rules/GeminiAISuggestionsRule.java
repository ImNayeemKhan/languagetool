package org.languagetool.rules;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import org.languagetool.AnalyzedSentence;
import org.languagetool.rules.Rule;
import org.languagetool.rules.RuleMatch;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Custom AI Rule using Gemini API for advanced suggestions.
 * Personalized for ImNayeemKhan's tech writing style (ISP platform docs).
 */
public class GeminiAISuggestionsRule extends Rule {

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent";
    private final String apiKey;

    public GeminiAISuggestionsRule(String apiKey) {
        this.apiKey = apiKey;
        setCategory(new Category("AI Suggestions", true));
    }

    @Override
    public String getId() {
        return "GEMINI_AI_SUGGESTIONS";
    }

    @Override
    public String getDescription() {
        return "Advanced AI writing suggestions powered by Gemini (personalized for your ISP/tech docs style)";
    }

    @Override
    public RuleMatch[] match(AnalyzedSentence sentence) {
        if (apiKey == null || apiKey.isEmpty()) {
            return new RuleMatch[0];
        }

        String text = sentence.getText();
        if (text.trim().length() < 10) return new RuleMatch[0];

        try {
            String prompt = buildPersonalizedPrompt(text);
            String response = callGemini(prompt);
            List<RuleMatch> matches = parseGeminiResponse(response, sentence);
            return matches.toArray(new RuleMatch[0]);
        } catch (Exception e) {
            // Log error silently
            return new RuleMatch[0];
        }
    }

    private String buildPersonalizedPrompt(String text) {
        return """You are an expert writing coach for Nayeem Khan, a Bangladeshi developer building ISP platforms in Dhaka.
Your style is clear, professional, practical, and structured — like these docs:
- Use tables for comparisons
- Step-by-step instructions with code blocks
- Practical deployment focus
- Technical yet approachable tone

Text to improve:
""" + text + """

Provide Grammarly-Premium level suggestions: clarity, tone, structure, technical accuracy, Bangla-English mixing fixes.
Return ONLY valid JSON:
{
  "suggestions": [
    {"issue": "short description", "fix": "suggested fix", "offset": startIndex, "length": length}
  ],
  "rewritten": "full rewritten version if major changes needed"
}""";
    }

    private String callGemini(String prompt) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        String jsonBody = "{\"contents\": [{\"parts\": [{\"text\": \"" + prompt.replace("\"", "\\\"") + "\"}]}]}";

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(GEMINI_API_URL + "?key=" + apiKey))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private List<RuleMatch> parseGeminiResponse(String jsonResponse, AnalyzedSentence sentence) {
        List<RuleMatch> matches = new ArrayList<>();
        try {
            JsonObject json = JsonParser.parseString(jsonResponse).getAsJsonObject();
            // Parse and create RuleMatch objects...
            // (Simplified for first version - expand later)
        } catch (Exception e) {}
        return matches;
    }

    // Add more methods as needed
}
