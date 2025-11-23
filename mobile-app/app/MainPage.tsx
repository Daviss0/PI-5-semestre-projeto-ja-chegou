import React, { useState, useRef, useEffect } from "react";
import { View, Text, StyleSheet, TextInput, TouchableOpacity, ScrollView } from "react-native";
import MapView, { Polyline, Marker, Region } from "react-native-maps";
import { Ionicons } from "@expo/vector-icons";

const SERVER_IP = "192.168.1.104";
const API = `http://${SERVER_IP}:8080/api/routes`;
const API_OV = `http://${SERVER_IP}:8080/api/olhoVivo`;

const LINE_COLORS: Record<string, string> = {
    "675P-10": "#FF0000","N634-11": "#FF5500","607A-10": "#FF8800","627M-10": "#FFCC00","745M-10": "#00FF00",
    "5129-10": "#00CC66","6030-10": "#00AAAA","6062-51": "#0099FF","6091-21": "#0066FF","6091-51": "#5500FF",
    "N631-11": "#9900FF","546L-10": "#CC00FF",
};

type BusInfo={px:number;py:number;p:string;a:boolean;ta:string;};
type RouteResult={routeId:string;shortName:string;longName:string;distanceToUser:number;shape:number[][];buses:BusInfo[];};

export default function MainPage(){
    const mapRef=useRef<MapView>(null);
    const [region,setRegion]=useState<Region>({latitude:-23.55052,longitude:-46.633308,latitudeDelta:0.05,longitudeDelta:0.05});
    const [query,setQuery]=useState("");
    const [suggestions,setSuggestions]=useState<any[]>([]);
    const [showSuggestions,setShowSuggestions]=useState(false);
    const [searched,setSearched]=useState(false);
    const [mapLocked,setMapLocked]=useState(false);
    const [pinLocation,setPinLocation]=useState({lat:-23.55052,lon:-46.633308});
    const [searchedPin,setSearchedPin]=useState<{lat:number;lon:number}|null>(null);
    const [closestRoutes,setClosestRoutes]=useState<RouteResult[]>([]);
    let debounceTimer:any=null;

    const formatSuggestion=(item:any)=>{
        const addr=item.display_name.split(",");
        return addr.slice(0,4).join(",").trim();
    };

    const fetchSuggestions=async(t:string)=>{
        if(debounceTimer)clearTimeout(debounceTimer);
        if(t.length<3){setSuggestions([]);return;}
        debounceTimer=setTimeout(async()=>{
            try{
                const url=`https://nominatim.openstreetmap.org/search?format=json&bounded=1&limit=5&viewbox=-46.825,-23.356,-46.365,-23.865&q=${encodeURIComponent(t)}`;
                const res=await fetch(url);
                if(!res.ok)return;
                const data=await res.json();
                setSuggestions(data.slice(0,5));
            }catch(e){}
        },250);
    };

    const handleSelectSuggestion=(item:any)=>{
        const lat=parseFloat(item.lat);const lon=parseFloat(item.lon);
        setQuery(formatSuggestion(item));
        setSuggestions([]);setShowSuggestions(false);
        setSearchedPin({lat,lon});setPinLocation({lat:9999,lon:9999});
        const r={latitude:lat,longitude:lon,latitudeDelta:0.009,longitudeDelta:0.009};
        setRegion(r);mapRef.current?.animateToRegion(r,800);
        setSearched(true);setMapLocked(true);
        loadClosestRoutes(lat,lon);
    };

    const handleSearch=async()=>{
        if(!query)return;
        try{
            const url=`https://nominatim.openstreetmap.org/search?format=json&bounded=1&limit=1&viewbox=-46.825,-23.356,-46.365,-23.865&q=${encodeURIComponent(query)}`;
            const res=await fetch(url);const data=await res.json();
            if(!data.length)return;
            handleSelectSuggestion(data[0]);
        }catch(e){}
    };

    const loadClosestRoutes=async(latOverride?:number,lonOverride?:number)=>{
        try{
            const baseLat=latOverride??searchedPin?.lat;
            const baseLon=lonOverride??searchedPin?.lon;
            if(!baseLat||!baseLon)return;
            const res=await fetch(`${API}/closest?lat=${baseLat}&lon=${baseLon}`);
            const baseRoutes:RouteResult[]=await res.json();
            const updated:RouteResult[]=[];
            for(let r of baseRoutes){
                try{
                    const ov=await fetch(`${API_OV}/veiculos/${r.shortName}`);
                    const dados=await ov.json();
                    const buses=dados?.vs??[];
                    updated.push({...r,buses:buses.map((b:any)=>({px:b.px,py:b.py,p:b.p,a:b.a,ta:b.ta}))});
                }catch{updated.push({...r,buses:[]});}
            }
            setClosestRoutes(updated);
        }catch(e){}
    };

    useEffect(()=>{
        if(!searched)return;
        loadClosestRoutes();
        const i=setInterval(loadClosestRoutes,1500);
        return()=>clearInterval(i);
    },[searched]);

    const handleBack=()=>{
        setClosestRoutes([]);setSearched(false);setMapLocked(false);
        setSearchedPin(null);setSuggestions([]);setQuery("");
    };

    return(
        <View style={styles.container}>
            <MapView ref={mapRef} style={styles.map} region={region} onRegionChangeComplete={(r)=>!mapLocked&&setRegion(r)}>
                {searched&&closestRoutes.map((r,i)=>(
                    <Polyline key={`shape-${i}`} strokeColor={LINE_COLORS[r.shortName]||"#00FF00"} strokeWidth={4}
                              coordinates={r.shape.map(([lat,lon])=>({latitude:lat,longitude:lon}))}/>
                ))}
                {searched&&closestRoutes.flatMap((route,i)=>route.buses.map((bus,j)=>(
                    <Marker key={`bus-${i}-${j}`} coordinate={{latitude:bus.py,longitude:bus.px}}>
                        <View style={{width:22,height:22,borderRadius:11,backgroundColor:LINE_COLORS[route.shortName],borderWidth:2,borderColor:"#FFF"}}/>
                    </Marker>
                )))}
                {searchedPin&&(
                    <Marker coordinate={{latitude:searchedPin.lat,longitude:searchedPin.lon}}>
                        <View style={styles.searchMarker}/>
                    </Marker>
                )}
            </MapView>

            {searched&&(
                <TouchableOpacity style={styles.backButton} onPress={handleBack}>
                    <Ionicons name="arrow-back" size={26} color="#FFF"/>
                </TouchableOpacity>
            )}

            {!searched&&(
                <>
                    <View style={styles.searchBox}>
                        <Ionicons name="search" size={20} color="#bbb"/>
                        <TextInput style={styles.input} placeholder="Digite o endereço" placeholderTextColor="#777"
                                   value={query}
                                   onChangeText={(t)=>{setQuery(t);setShowSuggestions(true);fetchSuggestions(t);}}
                                   onSubmitEditing={handleSearch}/>
                    </View>
                    {showSuggestions&&suggestions.length>0&&(
                        <View style={styles.suggestionContainer}>
                            {suggestions.map((item,i)=>(
                                <TouchableOpacity key={i} style={styles.suggestionItem} onPress={()=>handleSelectSuggestion(item)}>
                                    <Text style={styles.suggestionText}>{formatSuggestion(item)}</Text>
                                </TouchableOpacity>
                            ))}
                        </View>
                    )}
                </>
            )}

            {searched&&(
                <View style={styles.bottomSheet}>
                    <View style={styles.sheetHandle}/>
                    <ScrollView>
                        {closestRoutes.map((r,i)=>(
                            <View key={i} style={styles.routeCard}>
                                <View style={{flexDirection:"row",alignItems:"center",gap:10}}>
                                    <View style={{width:12,height:12,borderRadius:6,backgroundColor:LINE_COLORS[r.shortName]}}/>
                                    <Text style={styles.routeTitle}>{r.shortName} — {r.longName}</Text>
                                </View>
                                <Text style={styles.routeDistance}>Distância: {(r.distanceToUser*1000).toFixed(0)}m</Text>
                                <Text style={{color:"#999",marginTop:4}}>Ônibus ativos: {r.buses.length}</Text>
                                <TouchableOpacity style={styles.routeButton}><Text style={styles.routeButtonText}>Selecionar rota</Text></TouchableOpacity>
                            </View>
                        ))}
                    </ScrollView>
                </View>
            )}
        </View>
    );
}

const styles=StyleSheet.create({
    container:{flex:1,backgroundColor:"#0E0E10"},
    map:{flex:1},
    searchBox:{position:"absolute",top:50,alignSelf:"center",width:"90%",padding:14,borderRadius:14,flexDirection:"row",alignItems:"center",backgroundColor:"#1C1C1E",zIndex:30},
    input:{flex:1,fontSize:16,color:"#FFF",marginLeft:8},
    suggestionContainer:{position:"absolute",top:110,width:"90%",alignSelf:"center",backgroundColor:"#1C1C1E",borderRadius:14,zIndex:40,paddingVertical:4},
    suggestionItem:{padding:10,borderBottomWidth:1,borderBottomColor:"#333"},
    suggestionText:{color:"#EAEAEA"},
    backButton:{position:"absolute",top:50,left:20,backgroundColor:"rgba(0,0,0,0.5)",padding:10,borderRadius:50,zIndex:20},
    bottomSheet:{position:"absolute",bottom:0,left:0,right:0,height:"32%",backgroundColor:"#18181B",borderTopLeftRadius:20,borderTopRightRadius:20,paddingHorizontal:16,paddingTop:8},
    sheetHandle:{width:50,height:5,backgroundColor:"#555",borderRadius:3,alignSelf:"center",marginBottom:10},
    routeCard:{backgroundColor:"#222226",padding:12,borderRadius:12,marginVertical:6},
    routeTitle:{color:"#FFF",fontSize:15,fontWeight:"bold"},
    routeDistance:{color:"#AAA",marginTop:4},
    routeButton:{marginTop:10,backgroundColor:"#1A1A1D",paddingVertical:8,borderRadius:10,alignItems:"center"},
    routeButtonText:{color:"#FFF",fontSize:14,fontWeight:"bold"},
    searchMarker:{width:26,height:26,borderRadius:13,backgroundColor:"#00A3FF",borderColor:"#FFF",borderWidth:3},
});
