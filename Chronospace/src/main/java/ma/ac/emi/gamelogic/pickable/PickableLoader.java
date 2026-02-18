package ma.ac.emi.gamelogic.pickable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PickableLoader {

    private static class PickableDTO {
        String type;
        double dropProbability;
        double value;
        String spritePath;
        int width;
        int height;

        boolean isAnimated;
        int frameCount;
        double frameSpeed;
    }

    public static List<Pickable> loadPickables(String jsonPath) {
        Gson gson = new Gson();
        List<Pickable> prototypes = new ArrayList<>();

        try (Reader reader = new InputStreamReader(
                Objects.requireNonNull(PickableLoader.class.getResourceAsStream(jsonPath)))) {

            Type listType = new TypeToken<ArrayList<PickableDTO>>(){}.getType();
            List<PickableDTO> dtos = gson.fromJson(reader, listType);

            for (PickableDTO dto : dtos) {
                Pickable p = new Pickable();

                p.setType(PickableType.valueOf(dto.type));
                p.setDropProbability(dto.dropProbability);
                p.setValue(dto.value);
                p.setSpritePath(dto.spritePath);

                p.setAnimated(dto.isAnimated);
                p.setFrameCount(dto.frameCount);
                p.setFrameSpeed(dto.frameSpeed > 0 ? dto.frameSpeed : 0.1);

                p.setWidth(dto.width > 0 ? dto.width : 16);
                p.setHeight(dto.height > 0 ? dto.height : 16);

                p.initGraphics();

                p.setDrawn(false);
                prototypes.add(p);
            }

            System.out.println("Loaded " + prototypes.size() + " pickables from " + jsonPath);

        } catch (Exception e) {
            System.err.println("Failed to load pickables config.");
            e.printStackTrace();
        }

        return prototypes;
    }
}