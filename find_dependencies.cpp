#include <set>
#include <string>
#include <iostream>
#include <fstream>
#include <map>

void explore(const std::map<std::string, std::string>& lines, std::set<std::string>& output, std::set<std::string>& explored, const std::string& target){
    const std::string& line = lines.find(target)->second;
    size_t pos = -1;
    if(explored.find(target) != explored.end()) return;
    explored.insert(target);
    while ((pos = line.find(':', pos + 1)) != std::string::npos) {
        size_t end = line.find('/', pos);
        std::string ent = line.substr(pos + 1, end - pos - 1);
        if(ent[0] == 'T'){
            explore(lines, output, explored, ent);
        } else {
            output.insert(ent);
        }
    }
}

std::string unwind(const std::map<std::string, std::string>& lines, std::map<std::string, std::string>& explored, const std::string& target){
    auto it = explored.find(target);
    if(it != explored.end()) return it->second;
    
    const std::string& line = lines.find(target)->second;
    std::string mLine = line;
    size_t pos = -1;
    
    while ((pos = mLine.find(':', pos + 1)) != std::string::npos) {
        size_t end = mLine.find('/', pos);
        std::string ent = mLine.substr(pos + 1, end - pos - 1);
        if(ent[0] == 'T'){
            mLine.replace(pos, end - pos + 1, unwind(lines, explored, ent));
        }
    }

    explored[target] = mLine;

    return mLine;
}

int main() {
    std::ifstream f("expression.txt");
    std::string line;
    std::map<std::string, std::string> lines;
    while(getline(f, line)){
        if(line.length() == 0) continue;
        int pos = line.find('=');
        if(pos >= 1){
            std::string key = line.substr(0, pos - 1);
            std::string value = line.substr(pos + 2);
            lines[key] = value;
        }
    }
    std::ofstream of("deps.txt");
    for(int i=0; i<256; i++){
        std::cout << i << "\n";
        std::set<std::string> findings;
        std::set<std::string> explored;
        explore(lines, findings, explored, "y" + std::to_string(i));
        of << "y" << i <<"_deps["<<findings.size()<<"] = ";
        for(std::string str : findings){
            of << str << ", ";
        }
        of << "\n";
        of.flush();
    }
}