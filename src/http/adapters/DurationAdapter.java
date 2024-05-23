package http.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

public class DurationAdapter extends TypeAdapter<Duration> {
    @Override
    public void write(JsonWriter out, Duration value) throws IOException {
        if (value != null) {
            out.value(value.toMinutes());
        } else {
            out.value("null");
        }
    }

    @Override
    public Duration read(JsonReader in) throws IOException {
        String str = in.nextString();
        if (str.equals("null")) {
            return null;
        } else {
            return Duration.ofMinutes(Long.parseLong(in.nextString()));
        }
    }
}
