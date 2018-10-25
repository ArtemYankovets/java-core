package com.aya.streams.collectors;

/* Copyright (C) 2013-2018, Cyber Force Group s.r.o.,
 * Křemencova 186/7, Nove Město, 110 00 Praha 1, Czech Republic, info@sdk.finance
 * This file is part of the SDK.finance product. SDK.finance is the proprietary licensed software.
 * Unauthorised copying of this file, via any medium, modification and/or any type of its distribution is strictly
 * prohibited
 * Proprietary and confidential.
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Artem Yankovets
 * @version 1
 * @since 24.10.18
 */
public class Main {

    public static void main(String[] args) {

        Sample sample1 = new Sample("1", SampleType.TEMPORARY, "111");
        Sample sample2 = new Sample("3", SampleType.PERSISTED, "222");
        Sample sample3 = new Sample("1", SampleType.PERSISTED, "222");
        Sample sample4 = new Sample("2", SampleType.TEMPORARY, "333");


        Map<String, SampleDto> result = Stream.of(sample1, sample2, sample3, sample4)
                .collect(Collectors.toMap(s -> s.getProductId(), SampleDto::create, (oldVal, newVal) -> oldVal.update(oldVal, newVal)));

        result.entrySet().stream().forEach(e-> System.out.println(e));


    }


}

@AllArgsConstructor
@Getter
class Sample {
    private String productId;
    private SampleType type;
    private String merchantId;
}

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
class SampleDto {
    private String productId;
    private String pMerchantId;
    private String tMerchantId;

    public static SampleDto create(Sample sample) {
        SampleDto dto = new SampleDto();
        dto.productId = sample.getProductId();
        if (sample.getType() == SampleType.PERSISTED) {
            dto.pMerchantId = sample.getMerchantId();
        } else {
            dto.tMerchantId = sample.getMerchantId();
        }
        return dto;
    }

//    BiFunction<SampleDto, SampleDto, SampleDto> update = (oldDto, newDto) -> {
//        if (newDto.getPMerchantId() != null && oldDto.pMerchantId == null) {
//            oldDto.setPMerchantId(newDto.getPMerchantId());
//        }
//
//        if (newDto.getTMerchantId() != null && oldDto.tMerchantId == null) {
//            oldDto.setTMerchantId(newDto.getTMerchantId());
//        }
//        return oldDto;
//    };

    public SampleDto update(SampleDto oldVal, SampleDto newVal) {
        if (newVal.getPMerchantId() != null && oldVal.pMerchantId == null) {
            oldVal.setPMerchantId(newVal.getPMerchantId());
        }

        if (newVal.getTMerchantId() != null && oldVal.tMerchantId == null) {
            oldVal.setTMerchantId(newVal.getTMerchantId());
        }
        return oldVal;
    }
}

enum SampleType {
    PERSISTED,
    TEMPORARY
}