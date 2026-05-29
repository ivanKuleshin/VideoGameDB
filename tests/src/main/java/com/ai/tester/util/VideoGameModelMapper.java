package com.ai.tester.util;

import com.ai.tester.model.api.json.VideoGameApiModel;
import com.ai.tester.model.db.VideoGameDbModel;
import lombok.experimental.UtilityClass;

@UtilityClass
public class VideoGameModelMapper {

    public VideoGameApiModel toApiModel(VideoGameDbModel dbModel) {
        return new VideoGameApiModel(
            dbModel.getId(),
            dbModel.getName(),
            dbModel.getReleaseDateAsString(),
            dbModel.getReviewScore(),
            dbModel.getCategory(),
            dbModel.getRating()
        );
    }
}
