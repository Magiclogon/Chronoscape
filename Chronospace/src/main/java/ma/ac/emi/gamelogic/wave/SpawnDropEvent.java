package ma.ac.emi.gamelogic.wave;

import ma.ac.emi.math.Vector3D;

public record SpawnDropEvent(Vector3D position, int dropCount, double dropChanceOverride) {}