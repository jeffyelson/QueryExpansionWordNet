export interface ResultList {
    topicID: number;
    topicName: string;
    topicContent: string;
    topicDocument : string;
}

export interface SearchResponse {
    queryList: string[];
    synonymList: string[];
    resultList: ResultList[];
    count: number;
}
