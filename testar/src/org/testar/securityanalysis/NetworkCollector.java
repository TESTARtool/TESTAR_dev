package org.testar.securityanalysis;

import org.apache.logging.log4j.core.util.KeyValuePair;

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

    public void printData()
    {
        /*for (NetworkDataDto d : networkData)
        {
            System.out.println("Type: " + d.type);
            System.out.println("Sequence: " + d.sequence);
            System.out.println("RequestId: " + d.requestId);
            for (Map.Entry<String, String> e : d.data.entrySet()) {
                System.out.println(e.getKey() + ": " + e.getValue());
            }
            System.out.println("---------------------------");
        }*/
    }

    /*public void addData(String requestId, Map<String, String> data)
    {
        if (requests.get(requestId) == null){
            Map<String, String> dataList = new HashMap();
            dataList.putAll(data);
            requests.put(requestId, new ArrayList(dataList));
        }
        else {
            requests.get(requestId).add(data);
        }
    }*/

    /*public List<Map<String, String>> getData(String requestId)
    {
        return requests.get(requestId);
    }

    public Map<String, List<Map<String, String>>> getData()
    {
        return requests;
    }*/
}
