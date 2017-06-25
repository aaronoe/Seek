package de.aaronoe.seek.data.local;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by aaron on 29.05.17.
 */

public interface ImageColumns {

    @DataType(DataType.Type.TEXT)
    @PrimaryKey(onConflict = ConflictResolutionType.REPLACE)
    @AutoIncrement
    @NotNull
    public static final String COLUMN_ID = "id";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_USER_ID = "user_id";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    public static final String COLUMN_TIMESTAMP = "timestamp";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_USER_NAME = "user_name";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_USER_IMAGE_LINK = "user_image";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_RAW_IMAGE_LINK = "raw_link";

    @DataType(DataType.Type.TEXT)
    @NotNull
    public static final String COLUMN_REG_IMAGE_LINK = "reg_link";

}
