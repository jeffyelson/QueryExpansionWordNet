import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { AppConfigService } from '../app-config.service';
import { NewSearchService } from './new-search.service';
import { ResultList, SearchResponse } from './searchResponse';

@Component({
  selector: 'app-new-search',
  templateUrl: './new-search.component.html',
  styleUrls: ['./new-search.component.css']
})
export class NewSearchComponent implements OnInit {
  p: number = 1;
  @ViewChild(MatPaginator) paginator: MatPaginator | undefined;
  imageSrc = "/assets/images/logo.png";
  imageAlt = "aa";
  public dataSource: any;
  public pageSize = 10;
  public currentPage = 0;
  public totalSize = 0;
  showResult: boolean = false;
  searchResult: ResultList[] = [];
  result : SearchResponse = {} as SearchResponse;
  count: number = 0;
  apiBaseUrl: string = "";
  query: string = "";
  openNewTab: boolean = false;
  title: string | undefined;
  content: string | undefined;
  synonym: string[] = [];
  queryTerm: any;
  constructor(private appConfigService: AppConfigService, private newSearchService: NewSearchService) { }

  ngOnInit(): void {

  }

  search(): void {
    if (this.query !== "") {
      this.queryTerm = this.query;
      this.showResult = true;
      this.searchResult = [];
      this.newSearchService.fetchSearchResults(this.query).subscribe(response => {
        console.log("response");
        this.p=1;
        this.result = <SearchResponse>response;
        this.searchResult = (<SearchResponse>response).resultList;
        this.count = (<SearchResponse>response).count;
        this.synonym = (<SearchResponse>response).synonymList;
        this.totalSize = (<SearchResponse>response).resultList.length;
        this.dataSource = new MatTableDataSource<ResultList>((<SearchResponse>response).resultList);
        this.dataSource.paginator = this.paginator;
      },
        err => console.error(err),
      );
    }
  }

  openWindow(link: ResultList): void {
    this.openNewTab = true;
    this.title = link.topicName;
    this.content = link.topicDocument;
  }
}
