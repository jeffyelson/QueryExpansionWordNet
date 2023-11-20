import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root'
})
export class NewSearchService {

  constructor(private http:HttpClient) { }

  fetchSearchResults(query : string) : Observable<any>{
    let queryString = new HttpParams().set('searchString',query);
    return this.http.get('http://localhost:8080/search',{params:queryString});
  }
}
