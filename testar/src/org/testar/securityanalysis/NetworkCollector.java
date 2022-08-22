package org.testar.securityanalysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class NetworkCollector {
    private List<NetworkDataDto> networkData = new ArrayList<>();
    private int currentSequence = 0;

    public void addData(NetworkDataDto data)
    {
        data.sequence = currentSequence++;
        networkData.add(data);
    }

    private Map<String, Map<String, String>> requests = new HashMap<>();

    public List<NetworkDataDto> getData()
    {
        return networkData;
    }

    public List<NetworkDataDto> getDataBySequence(int sequence)
    {
        return networkData.stream().filter(d -> d.sequence > sequence).collect(Collectors.toList());
    }
}
