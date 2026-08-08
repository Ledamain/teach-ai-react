import { Input, Tag } from 'antd';
import { useEffect, useState } from 'react';

export default function OutlineEditor({ data, onChange }: any) {
    const [list, setList] = useState<any[]>([]);

    useEffect(() => {
        if (!data) return;

        const lines = data.split('\n');

        const parsed = lines.map((line: string, i: number) => {
            const level = (line.match(/^#+/) || [''])[0].length - 1;
            return {
                id: i,
                level,
                content: line.replace(/^#+\s*/, ''),
            };
        });

        setList(parsed);
    }, [data]);

    const update = (index: number, value: string) => {
        const newList = [...list];
        newList[index].content = value;
        setList(newList);

        const md = newList
            .map((n) => `${'#'.repeat(n.level + 1)} ${n.content}`)
            .join('\n');

        onChange(md);
    };

    return (
        <div>
            {list.map((item, i) => (
                <div key={i} style={{ marginLeft: item.level * 20 }}>
                    <Tag>Lv{item.level}</Tag>
                    <Input
                        value={item.content}
                        onChange={(e) => update(i, e.target.value)}
                    />
                </div>
            ))}
        </div>
    );
}
