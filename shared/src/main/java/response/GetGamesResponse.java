package response;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

public record GetGamesResponse(Collection<GameData> games) {
}
