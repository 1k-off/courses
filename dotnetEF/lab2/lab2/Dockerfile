#See https://aka.ms/containerfastmode to understand how Visual Studio uses this Dockerfile to build your images for faster debugging.

FROM mcr.microsoft.com/dotnet/runtime:6.0 AS base
WORKDIR /app

FROM mcr.microsoft.com/dotnet/sdk:6.0 AS build
WORKDIR /src
COPY ["lab2/lab2.csproj", "lab2/"]
RUN dotnet restore "lab2/lab2.csproj"
COPY . .
WORKDIR "/src/lab2"
RUN dotnet build "lab2.csproj" -c Release -o /app/build

FROM build AS publish
RUN dotnet publish "lab2.csproj" -c Release -o /app/publish /p:UseAppHost=false

FROM base AS final
WORKDIR /app
COPY --from=publish /app/publish .
ENTRYPOINT ["dotnet", "lab2.dll"]